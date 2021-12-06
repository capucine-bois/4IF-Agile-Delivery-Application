package controller;

public class ReverseCommand implements Command {

    private Command cmd;

    /**
     * Creates the reverse command of cmd.
     */
    public ReverseCommand(Command cmd) { this.cmd = cmd;}

    @Override
    public void doCommand() {
        cmd.undoCommand();
    }

    @Override
    public void undoCommand() {
      cmd.doCommand();
    }
}
