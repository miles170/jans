#!/usr/bin/env python3

import sys
from asciimatics.widgets import Frame, ListBox, Layout, Label, Divider, Text, \
    Button, TextBox, Widget, CheckBox, DropdownList, ListBox, RadioButtons , PopupMenu
from asciimatics.widgets.popupdialog import PopUpDialog

from asciimatics.scene import Scene
from asciimatics.screen import Screen
from asciimatics.exceptions import ResizeScreenError, NextScene, StopApplication
# from my_checkbox import checkBoxList
from asciimatics.event import MouseEvent

import locale
import gettext
import os


language = 'en'  ### en + de

try:
    current_locale, encoding = locale.getdefaultlocale()
    language = gettext.translation (language, 'locale/', languages=[language] )
    language.install()
except:
    pass

_ = gettext.gettext



help_text_dict = {
    'displayName': _("Name of the user suitable for display to end-users"),
    'clientSecret': _("The client secret. The client MAY omit the parameter if the client secret is an empty string"),
    'redirectUris': _("Redirection URI values used by the Client. One of these registered Redirection URI values must exactly match the redirect_uri parameter value used in each Authorization Request"),
    'responseTypes': _("A list of the OAuth 2.0 response_type values that the Client is declaring that it will restrict itself to using. If omitted, the default is that the Client will use only the code Response Type. Allowed values are code, token, id_token"),
    'applicationType': _("Kind of the application. The default, if omitted, is web. The defined values are native or web. Web Clients using the OAuth Implicit Grant Type must only register URLs using the HTTPS scheme as redirect_uris, they must not use localhost as the hostname. Native Clients must only register redirect_uris using custom URI schemes or URLs using the http scheme with localhost as the hostname"),
    }

class CLIFrame(Frame):

    def __init__(self, screen, title=_('CLI Frame')):
        super().__init__(screen,
                                   screen.height,
                                   screen.width,
                                   has_border=True,
                                   can_scroll=False,
                                   )

        self.set_theme("bright")
        self.title = title
        self.layout = Layout([100], fill_frame=True)
        self.add_layout(self.layout)

        self.button_layout = Layout([1, 1, 1, 1])
        self.add_layout(self.button_layout)

        self.status = Layout([1])
        self.add_layout(self.status)
        self.status.add_widget(Divider())
        self.help_text = Label(_('Help'))
        self.status.add_widget(self.help_text)


    def update_help(self):
        cur_widget = self.layout.get_current_widget()
        help_key = cur_widget.name.split(':')[0]
        self.help_text.text = help_text_dict.get(help_key, help_key)
        self.help_text.update(None)
      

    def reset(self):
        super(CLIFrame, self).reset()


    def checkBoxButtons(self, label, name, items):
        for i, o in enumerate(items):
            labels = label if i==0 else _('')
            self.layout.add_widget(CheckBox(label=labels, text=o, name="{}:".format(name, o), on_focus=self.update_help))


    def radionButtons(self, label, name, items):
        self.layout.add_widget(RadioButtons(items, label=label, name=name, on_focus=self.update_help))




class OpenIDClientForm(CLIFrame):
    def __init__(self, screen):
        CLIFrame.__init__(self, screen, _("OpenID Client"))

        self.layout.add_widget(Text(label=_("Display Name:"), name='displayName', on_focus=self.update_help))
        self.layout.add_widget(Text(label=_("Client Secret:"), name="clientSecret", on_focus=self.update_help))
        self.layout.add_widget(TextBox(label=_("Redirect Uris:"), name='redirectUris', height=3, on_focus=self.update_help, as_string=True))

        self.checkBoxButtons(label=_("Response Types:"), name='responseTypes', items=('code', 'token', 'id_token'))
        self.radionButtons(label=_("Application Type"), name='applicationType', items=[('web', 'web'), ('native', 'native')])

        self.button_layout.add_widget(Button(_("OK"), self._ok, name='ok'), 0)
        self.button_layout.add_widget(Button(_("Next"), self._next, name='next'), 3)

        self.fix()


    def _ok(self):
        self.save()
        raise NextScene(_("Main"))

    def _next(self):
        raise NextScene(_("Test"))


class TestForm(CLIFrame):
    def __init__(self, screen):
        CLIFrame.__init__(self, screen, _("Test Form"))
        self.layout.add_widget(Text(_("Test Name :"), _("testName"), on_focus=self.update_help))
        self.fix()



def jans_cli_app(screen, scene):
    scenes = [
        Scene([OpenIDClientForm(screen)], -1, name=_("OpenIDClientForm")),
        Scene([TestForm(screen)], -1, name=_("Test")),
    ]
    screen.play(scenes, stop_on_resize=True, start_scene=scene, allow_int=True)


last_scene = None

while True:
    try:
        Screen.wrapper(jans_cli_app, catch_interrupt=True, arguments=[last_scene])
        sys.exit(0)
    except ResizeScreenError as e:
        last_scene = e.scene
