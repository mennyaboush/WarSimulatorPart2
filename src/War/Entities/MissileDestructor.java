package War.Entities;

import java.util.HashMap;

public class MissileDestructor extends Destructor<Missile>{

    public MissileDestructor(){
        super();
    }

    public MissileDestructor(String id){
        super(id);
    }

    private HashMap<Missile, Long> destructedMissiles = new HashMap<>();

    //Noa: there is no need to override this function!
    /*@Override
    public void destruct(Missile destructibleWeapon) {
        addDestructedWeapon(destructibleWeapon);
        destructibleWeapon.destruct();
    }*/
}
