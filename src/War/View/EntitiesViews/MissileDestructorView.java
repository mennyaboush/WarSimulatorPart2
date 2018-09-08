package War.View.EntitiesViews;

import War.Entities.MissileDestructor;
import War.View.MainWindowController;

public class MissileDestructorView extends WeaponView<MissileDestructor> {
    private static final String IMAGE_PATH = "War/View/images/icecreamtruck.png";
    public MissileDestructorView(MissileDestructor weapon) {
        super(weapon, IMAGE_PATH);
    }
}
