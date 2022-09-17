import json
from asyncio import Future
from typing import OrderedDict

from prompt_toolkit.widgets import Button, TextArea
from prompt_toolkit.application.current import get_app
from prompt_toolkit.layout.dimension import D
from static import DialogResult
from wui_components.jans_dialog import JansDialog
from prompt_toolkit.layout.containers import (
    VSplit,
    DynamicContainer,
)
from prompt_toolkit.key_binding import KeyBindings

from prompt_toolkit.widgets import (
    Button,
    Label,
    TextArea,

)
from prompt_toolkit.widgets import (
    Button,
    Dialog,
    VerticalLine,
)
from cli import config_cli
from prompt_toolkit.layout.containers import (
    ConditionalContainer,
    Float,
    HSplit,
    VSplit,
    VerticalAlign,
    DynamicContainer,
    FloatContainer,
    Window
)
from prompt_toolkit.widgets import (
    Box,
    Button,
    Frame,
    Label,
    RadioList,
    TextArea,
    CheckboxList,
    Shadow,
)
from wui_components.jans_dialog_with_nav import JansDialogWithNav
from wui_components.jans_nav_bar import JansNavBar
from wui_components.jans_side_nav_bar import JansSideNavBar
from utils import DialogUtils

from wui_components.jans_cli_dialog import JansGDialog

from wui_components.jans_drop_down import DropDownWidget

from multi_lang import _


class ViewUMADialog(JansGDialog, DialogUtils):

    def __init__(self, parent, title, data, buttons=[], save_handler=None):

        super().__init__(parent, title, buttons)
        self.save_handler = save_handler
        self.data = data
        self.UMA_containers = {}
        self.UMA_prepare_containers()


        def delete():
            self.myparent.show_again()
            # self.future.set_result(DialogResult.CANCEL)

        def cancel():
            self.future.set_result(DialogResult.CANCEL)

        self.side_nav_bar =  JansNavBar(
                    self,
                    entries=[('scope', 'scope'), ('expression', 'scope expression'), ],
                    selection_changed=self.oauth_nav_selection_changed,
                    select=0,
                    bgcolor='#8a8a8a'
                    )


        self.dialog = Dialog(title='UMA-resources',

        body=     
        HSplit([

            self.myparent.getTitledText(
                                "Resource id",
                                name='id',
                                value=self.data.get('id',''),
                                read_only=True,
                                style='green',
                            ),

            self.myparent.getTitledText(
                                "Display Name",
                                name='name',
                                value=self.data.get('name',''),
                                read_only=True,
                                style='green'),

            self.myparent.getTitledText(
                                "IconURL",
                                name='iconUri',
                                value=self.data.get('iconUri',''),
                                read_only=True,
                                style='green'),    


            VSplit([
            Label(text=_("Scope Selection"),style='green bold',width=len(_("Scope Selection"))), ## TODO dont know what is that

            Box(self.side_nav_bar.nav_window, style='fg:#4D4D4D bg:#ffffff', height=1),

            ]),
            
            DynamicContainer(lambda: self.oauth_main_area),
            
            self.myparent.getTitledText(
                                "Associated Client",
                                name='clients',
                                value=self.data.get('clients',''),
                                read_only=True,
                                style='green'), 

            self.myparent.getTitledText(
                                "Creation time",
                                name='creationDate',
                                value=self.data.get('creationDate',''),
                                read_only=True,
                                style='green'), 

        ], padding=1,width=100,
        # key_bindings=self.get_uma_dialog_key_bindings()
        ),
        buttons=[
                Button(
                    text="Cancel",
                    handler=cancel,
                ) ,
                Button(
                    text="Delete",
                    handler=delete,
                ) 
            ],
            with_background=False,
            # width=140,
        )


    def UMA_prepare_containers(self):
        
        self.oauth_main_area =  self.UMA_containers['scope'] = HSplit([
        self.myparent.getTitledText(
                                "Scopes",
                                name='scope',
                                value=self.data.get('scope',[]),
                                read_only=True,
                                style='green',
                            )

        ],width=D())
        
        

        self.UMA_containers['expression'] = HSplit([
        self.myparent.getTitledText(
                                "Expression",
                                name='expression',
                                value=self.data.get('expression',[]),
                                read_only=True,
                                style='green',
                            ),

        ],width=D())
        


    
    def oauth_nav_selection_changed(self, selection):
        """This method for the selection change

        Args:
            selection (str): the current selected tab
        """
        if selection in self.UMA_containers:
            self.oauth_main_area = self.UMA_containers[selection]
        
        

    def save(self):
        pass

    def __pt_container__(self):
        return self.dialog

