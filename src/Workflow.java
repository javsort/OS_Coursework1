package src;

/*
 * Workflow.java.
 * This interface is used to define the workflow of the system, which is the way the system will work.
 * It will be implemented whether for a workflow with a single Sensor or multiple.
 */
public interface Workflow {
    // Workflow methods
    public String name();
    public void init();
    public void go();
    public void enterValues();
}
