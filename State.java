import java.io.Serializable;

public class State implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean isFinal;

    public State(String name, boolean isFinal) {
        this.name = name.toLowerCase();
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name.toLowerCase();}

    public boolean isFinal() {
        return isFinal;
    }
    public void setFinal(boolean isFinal) {this.isFinal = isFinal;}

    @Override
    public String toString() {
        return name + (isFinal ? " (final)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State state = (State) obj;
        return name.equals(state.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
