/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.succinct.beckn.portal.menu;

import com.venky.swf.menu.DefaultMenuBuilder;
import com.venky.swf.path.Path;
import com.venky.swf.path._IPath;
import com.venky.swf.views.controls.model.ModelAwareness;
import com.venky.swf.views.controls.page.Menu;
import com.venky.swf.views.controls.page.Menu.SubMenu;

/**
 *
 * @author venky
 */
public class AppMenuBuilder extends DefaultMenuBuilder{
    @Override
    public Menu createAppMenu(_IPath path) {
        Menu appMenu = super.createAppMenu(path);
        SubMenu subMenu = appMenu.getSubmenu("Help");
        subMenu.addMenuItem("Getting Started", "/markdown/getting_started?includeMenu=Y");
        subMenu.addMenuItem("Api Documentation", "/markdown/api_documents?includeMenu=Y");
        return appMenu;
    }
}
