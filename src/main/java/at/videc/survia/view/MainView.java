package at.videc.survia.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Route("")
@Theme(value = Material.class, variant = Material.DARK)
@CssImport("./styles/shared-styles.css")
@Push
public class MainView extends AppLayout implements BeforeEnterObserver {

    private final static Logger LOG = LoggerFactory.getLogger(MainView.class);

    private Tabs tabs = new Tabs();
    private Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    public MainView() {
        addMenuTab(new Icon(VaadinIcon.COG), "admin", AdminView.class);
        addMenuTab(new Icon(VaadinIcon.DATABASE), "datasets", DatasetsView.class);
        addMenuTab(new Icon(VaadinIcon.STETHOSCOPE), "indicators", IndicatorsView.class);
        addMenuTab(new Icon(VaadinIcon.SPARK_LINE), "observations", ObservationsView.class);
        addMenuTab(new Icon(VaadinIcon.ABSOLUTE_POSITION), "measure units", MeasureUnitsView.class);
        addMenuTab(new Icon(VaadinIcon.GLOBE), "countries", CountriesView.class);
        addMenuTab(new Icon(VaadinIcon.USERS), "users", UsersView.class);
        addMenuTab(new Icon(VaadinIcon.USER_CARD), "roles", RolesView.class);

        tabs.add(new Tab(new Icon(VaadinIcon.SIGN_OUT), new Anchor("logout", "logout")));

        addToNavbar(tabs);
    }

    private void addMenuTab(Icon icon, String label, Class<? extends Component> target) {
        // TODO click on icons does not change the url -> no navigation
        // change to Buttons an clickHandler instead?
        Tab tab = new Tab(icon, new RouterLink(label, target));
        navigationTargetToTab.put(target, tab);
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
    }

}
