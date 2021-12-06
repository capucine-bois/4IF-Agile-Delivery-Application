package controller;


import java.util.LinkedList;

/**
 * Represent every command done, and that can be canceled, redone or undone.
 */
public class ListOfCommands {
    private final LinkedList<Command> list;

    private int currentIndex;

    public ListOfCommands(){
        currentIndex = -1;
        list = new LinkedList<>();
    }

    /**
     * Add command c to this
     * @param c the command to add
     */
    public void add(Command c){
        int i = currentIndex+1;
        while(i<list.size()){
            list.remove(i);
        }
        currentIndex++;
        list.add(currentIndex, c);
        c.doCommand();
    }

    /**
     * Temporary remove the last added command (this command may be reinserted again with redo)
     */
    public void undo(){
        if (currentIndex >= 0){
            Command cde = list.get(currentIndex);
            currentIndex--;
            cde.undoCommand();
        }
    }

    /**
     * Reinsert the last command removed by undo
     */
    public void redo(){
        if (currentIndex < list.size()-1){
            currentIndex++;
            Command cde = list.get(currentIndex);
            cde.doCommand();
        }
    }

    /**
     * Permanently remove all commands from the list
     */
    public void reset(){
        currentIndex = -1;
        list.clear();
    }


    public int getCurrentIndex() {
        return currentIndex;
    }

    public int size(){
        return list.size();
    }
}
