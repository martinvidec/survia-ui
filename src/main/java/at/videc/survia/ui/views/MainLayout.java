package at.videc.survia.ui.views;

import at.videc.survia.ui.data.User;
import at.videc.survia.ui.configuration.security.AuthenticatedUser;
import at.videc.survia.ui.views.admin.AdminView;
import at.videc.survia.ui.views.codedvalues.CodedValuesView;
import at.videc.survia.ui.views.countries.CountriesView;
import at.videc.survia.ui.views.datasets.DatasetsView;
import at.videc.survia.ui.views.hello.HelloView;
import at.videc.survia.ui.views.indicators.IndicatorsView;
import at.videc.survia.ui.views.measurementunits.MeasurementUnitsView;
import at.videc.survia.ui.views.observations.ObservationsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Survia");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(HelloView.class)) {
            nav.addItem(new SideNavItem("Hello", HelloView.class, LineAwesomeIcon.GLOBE_SOLID.create()));

        }
        if (accessChecker.hasAccess(DatasetsView.class)) {
            nav.addItem(new SideNavItem("Datasets", DatasetsView.class, LineAwesomeIcon.DATABASE_SOLID.create()));

        }
        if (accessChecker.hasAccess(IndicatorsView.class)) {
            nav.addItem(new SideNavItem("Indicators", IndicatorsView.class, LineAwesomeIcon.HAND_PAPER_SOLID.create()));

        }
        if (accessChecker.hasAccess(ObservationsView.class)) {
            nav.addItem(
                    new SideNavItem("Observations", ObservationsView.class, LineAwesomeIcon.HEARTBEAT_SOLID.create()));

        }
        if (accessChecker.hasAccess(MeasurementUnitsView.class)) {
            nav.addItem(new SideNavItem("Measurement Units", MeasurementUnitsView.class,
                    LineAwesomeIcon.RULER_COMBINED_SOLID.create()));

        }
        if (accessChecker.hasAccess(CodedValuesView.class)) {
            nav.addItem(new SideNavItem("Coded Values", CodedValuesView.class,
                    LineAwesomeIcon.CODE_SOLID.create()));

        }
        if (accessChecker.hasAccess(CountriesView.class)) {
            nav.addItem(new SideNavItem("Countries", CountriesView.class, LineAwesomeIcon.GLOBE_SOLID.create()));

        }
        if (accessChecker.hasAccess(AdminView.class)) {
            nav.addItem(new SideNavItem("Admin", AdminView.class, LineAwesomeIcon.COG_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getUsername());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getUsername());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> authenticatedUser.logout());

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
