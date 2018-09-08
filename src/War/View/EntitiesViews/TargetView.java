package War.View.EntitiesViews;

import War.Entities.Destination;
import War.View.MainWindowController;

public class TargetView extends WeaponView<Destination>{

    public TargetView(Destination weapon) {
        super(weapon, "War/View/images/Target_small.png");
    }
}
