package War.Entities;

import java.io.Serializable;

public class LauncherDestructor extends Destructor<Launcher> implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public enum DestructorType {PLANE, SHIP}
    private static int idGenerator = 10;

    private DestructorType type;

    public LauncherDestructor(DestructorType type) {
        super("LD"+idGenerator++);
        setType(type);
    }


    public DestructorType getType() {
        return type;
    }

    public void setType(DestructorType type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return String.format("%s [%6s]",super.toString(),getType().toString());
    }
}
