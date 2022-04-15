#!/usr/bin/python3
import warnings
warnings.filterwarnings("ignore")

import readline
import os
import sys
import time
import glob
import inspect
import zipfile
import shutil
import traceback
import argparse
import ldap3
import pathlib
import json
import zipfile

from urllib import request

from setup_app import paths
from setup_app import static
from setup_app.utils import base
from setup_app.config import Config
from setup_app.pylib import schema

from setup_app.utils.ldif_utils import myLdifParser
from setup_app.installers.scim import ScimInstaller
from setup_app.installers.jans import JansInstaller
from setup_app.installers.config_api import ConfigApiInstaller
from setup_app.test_data_loader import TestDataLoader

cur_dir = os.path.dirname(os.path.realpath(__file__))
#salt pXwnQ5s3lIMfdPcoHYGwy9rw

parser = argparse.ArgumentParser()

parser.add_argument('-salt', help='Salt for encoding passwords', required=True)
parser.add_argument('-yes', help='"yes" to all prompts', action='store_true')
parser.add_argument('-output-dir', help='Outut directory for rendered templates', default='/opt/output')
parser.add_argument('-java-home', help='Java home direcory. For example: /usr/lib/jvm/java-11-openjdk-amd64')
parser.add_argument('-jans-auth-client-jar', help='Jans auth client with dependencies jar file path')


subparsers = parser.add_subparsers(help='help for subcommand', dest="subcommand")

parser_ldap = subparsers.add_parser('ldap', help='Load test data to LDAP')
parser_ldap.add_argument('-ldap-host', help='LDAP hostname', required=True)
parser_ldap.add_argument('-ldap-port', type=int, help='LDAP port', default=1636)
parser_ldap.add_argument('-ldap-admin-user', help='LDAP username', default="cn=directory manager")
parser_ldap.add_argument('-ldap-admin-password', help='LDAP admin user password', required=True)


argsp = parser.parse_args()
argsp.t = True

base.argsp = argsp

if not os.path.exists(argsp.output_dir):
    os.mkdir(argsp.output_dir)



Config.outputFolder = argsp.output_dir
# initialize config object
Config.init(paths.INSTALL_DIR)
Config.encode_salt = argsp.salt
Config.currentJansVersion = 'test-data'
Config.oxVersion = '1.0.0-SNAPSHOT'
Config.remote_test_data = True


app_dir = os.path.join(Config.distFolder, 'app')

def download_gcs():
    if not os.path.exists(os.path.join(app_dir, 'gcs')):
        print("Downloading Spanner modules")
        gcs_download_url = 'https://ox.gluu.org/icrby8xcvbcv/spanner/gcs.tgz'
        tmp_dir = os.path.join(app_dir, 'gcs-' + os.urandom(5).hex())
        target_fn = os.path.join(tmp_dir, 'gcs.tgz')
        base.download(gcs_download_url, target_fn, verbose=True)
        shutil.unpack_archive(target_fn, app_dir)

        req = request.urlopen('https://pypi.org/pypi/grpcio/1.37.0/json')
        data_s = req.read()
        data = json.loads(data_s)

        pyversion = 'cp{0}{1}'.format(sys.version_info.major, sys.version_info.minor)

        package = {}

        for package_ in data['urls']:

            if package_['python_version'] == pyversion and 'manylinux' in package_['filename'] and package_['filename'].endswith('x86_64.whl'):
                if package_['upload_time'] > package.get('upload_time',''):
                    package = package_

        if package.get('url'):
            target_whl_fn = os.path.join(tmp_dir, os.path.basename(package['url']))
            base.download(package['url'], target_whl_fn, verbose=True)
            whl_zip = zipfile.ZipFile(target_whl_fn)

            for member in  whl_zip.filelist:
                fn = os.path.basename(member.filename)
                if fn.startswith('cygrpc.cpython') and fn.endswith('x86_64-linux-gnu.so'):
                    whl_zip.extract(member, os.path.join(app_dir, 'gcs'))

            whl_zip.close()

        shutil.rmtree(tmp_dir)


download_gcs()


if argsp.jans_auth_client_jar:
    Config.non_setup_properties['oxauth_client_jar_fn'] = argsp.jans_auth_client_jar


target_auth_client_fn = os.path.join(argsp.output_dir, 'jans-auth-client-with-dependencies.jar')

if os.path.exists(target_auth_client_fn):
    Config.non_setup_properties['oxauth_client_jar_fn'] = target_auth_client_fn
elif not os.path.exists(Config.non_setup_properties['oxauth_client_jar_fn']):
    print(Config.non_setup_properties['oxauth_client_jar_fn'])
    print("Can't find Jans auth client with dependencies.")
    result = 'y' if argsp.yes else input("Download Now? [Y/n]")
    if result.lower().startswith('y'):
        base.download('https://maven.jans.io/maven/io/jans/jans-auth-client/{0}/jans-auth-client-{0}-jar-with-dependencies.jar'.format(Config.oxVersion), target_fn, verbose=True)
        Config.non_setup_properties['oxauth_client_jar_fn'] = target_auth_client_fn
    else:
        print("Exiting ...")
        sys.exit()

if argsp.subcommand == 'ldap':
    Config.hostname = argsp.ldap_host
    Config.ldapPass = argsp.ldap_admin_password
    Config.ldap_hostname = argsp.ldap_host
    Config.persistence_type = 'ldap'


if argsp.java_home:
    java_home = pathlib.Path(argsp.java_home)
    java_path = java_home.joinpath('bin/java')
    if java_path.exists():
        Config.cmd_java = java_path.as_posix()
        Config.cmd_keytool = java_home.joinpath('bin/keytool').as_posix()
else:
    Config.cmd_java = shutil.which('java')
    Config.cmd_keytool = shutil.which('keytool')

if not (os.path.exists(Config.cmd_java) and os.path.exists(Config.cmd_keytool)):
    print("Can't locate java. Please provide -java-path")
    sys.exit()

jansInstaller = JansInstaller()
jansInstaller.initialize()
test_data_loader = TestDataLoader()


try:
    test_data_loader.dbUtils.bind()
except ldap3.core.exceptions.LDAPSocketOpenError:
    print("Can't connect to LDAP server on", Config.hostname)
    print("Please execute this command on server and restart opendj")
    print('/opt/opendj/bin/dsconfig --trustAll --no-prompt --hostname localhost --port 4444 --bindDN "cn=directory manager" -w Gluu1234. set-connection-handler-prop --handler-name "LDAPS Connection Handler" --set enabled:true --set listen-address:0.0.0.0')
    sys.exit()


server_schema = test_data_loader.dbUtils.ldap_conn.server.schema
jans_custom_person_oc = server_schema.object_classes['jansCustomPerson']

jans_custom_person_oc_schema_file = None
for ext_key, ext_val in jans_custom_person_oc.extensions:
    if ext_key == 'X-SCHEMA-FILE':
        jans_custom_person_oc_schema_file = ext_val[0]

def add_schema_file(attr_s, filename):
    if not filename:
        return
    attr_s = attr_s.strip()
    return attr_s.rstrip(')') + "X-SCHEMA-FILE '" + os.path.basename(filename) + "' )"

print("Updating opendj schema remotely")
for schema_fn in (test_data_loader.oxauth_test_schema_fn, test_data_loader.scim_test_schema_fn):
    parser = myLdifParser(schema_fn)
    parser.parse()

    for dn, entry in parser.entries:
        if dn == 'cn=schema':
            for attr_type in entry.get('attributeTypes', []):
                attrs = add_schema_file(attr_type, schema_fn)
                attr_obj = schema.AttributeType(attr_type)
                print("Adding Attribute", attr_obj.tokens['NAME'][0])
                test_data_loader.dbUtils.ldap_conn.modify("cn=schema", {'attributeTypes': [ldap3.MODIFY_ADD, attrs]})
            for object_class in entry.get('objectClasses', []):
                object_s = add_schema_file(object_class, schema_fn)
                obj_obj = schema.ObjectClass(object_class)
                print("Adding Object Class", obj_obj.tokens['NAME'][0])
                test_data_loader.dbUtils.ldap_conn.modify("cn=schema", {'objectClasses': [ldap3.MODIFY_ADD, object_s]})

jans_custom_person_object = schema.ObjectClass(jans_custom_person_oc.raw_definition)

may_contain = list(jans_custom_person_object.tokens['MAY'])
scim_attributes = []
for scim_attr in ('scimCustomFirst', 'scimCustomSecond', 'scimCustomThird'):
    if scim_attr not in may_contain:
        scim_attributes.append(scim_attr)

if scim_attributes:
    may_contain += scim_attributes
    jans_custom_person_object.tokens['MAY'] = tuple(may_contain)
    jans_custom_person_object_s = jans_custom_person_object.getstr()
    jans_custom_person_object_s = add_schema_file(jans_custom_person_object_s, jans_custom_person_oc_schema_file)
    print("Deleting Object Class", jans_custom_person_object.tokens['NAME'][0])
    result = test_data_loader.dbUtils.ldap_conn.modify("cn=schema", {'objectClasses': [ldap3.MODIFY_DELETE, jans_custom_person_oc.raw_definition]})
    print("Adding Object Class", jans_custom_person_object.tokens['NAME'][0], "with new attrbiutes:", ', '.join(scim_attributes))
    result = test_data_loader.dbUtils.ldap_conn.modify("cn=schema", {'objectClasses': [ldap3.MODIFY_ADD, jans_custom_person_object_s]})

print("Chcking if scim is installed")
result = test_data_loader.dbUtils.dn_exists("ou=jans-scim,ou=configuration,o=jans")

if not result:
    print("Scim was not installed on target server. Please install scim and re-run this script")
    sys.exit()

class ScimInstaller:
    def installed(self):
        return True

base.current_app.ScimInstaller = ScimInstaller()

print("Checking clients")
client_var_id_list = [
                    #('oxauth_client_id', '1001.'),
                    ('jca_client_id', '1800.', {'pw': 'jca_client_pw', 'encoded':'jca_client_encoded_pw'}),
                    ('jca_test_client_id', '1802.', {'pw': 'jca_test_client_pw', 'encoded':'jca_test_client_encoded_pw'}),
                    ('scim_client_id', '1201.', {'pw': 'scim_client_pw', 'encoded':'scim_client_encoded_pw'}),
                    #('admin_ui_client_id', '1901.', {'pw': 'admin_ui_client_pw', 'encoded': 'admin_ui_client_encoded_pw'}),
                    ]
test_data_loader.check_clients(client_var_id_list, create=False)

config_api_installer = ConfigApiInstaller()
config_api_installer.installed = lambda: True

if not Config.get('jca_test_client_id'):
    print("Loading Config Api test data")
    config_api_installer.load_test_data()


if not Config.templateRenderingDict.get('config_api_scopes_list'):
    config_api_installer.prepare_scope_list()

print("Loading test data")
test_data_loader.load_test_data()

print("Rendered templates were written to", argsp.output_dir)
