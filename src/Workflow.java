package src;

public interface Workflow {
    public String name();
    public void init();
    public void go();
    public void enterValues();
}
