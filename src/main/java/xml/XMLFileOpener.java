package xml;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

/**
 * Singleton to open an XML file and store instance.
 */
public class XMLFileOpener extends FileFilter {// Singleton

    private static XMLFileOpener instance = null;

    /**
     * Default constructor
     */
    private XMLFileOpener(){}

    /**
     * Getter for instance attribute. Instantiate singleton if not already instantiated.
     * @return XMLFileOpener instance
     */
    protected static XMLFileOpener getInstance(){
        if (instance == null) instance = new XMLFileOpener();
        return instance;
    }

    /**
     * Open file specified by user on JFileChooser GUI element.
     * @param read indicate if the file to open has already been selected
     * @return path of opened file
     * @throws ExceptionXML raised if file can't be read
     */
    public File open(boolean read) throws ExceptionXML{
        int returnVal;
        JFileChooser jFileChooserXML = new JFileChooser("src/main/resources/fichiersXML2020");
        jFileChooserXML.setFileFilter(this);
        jFileChooserXML.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (read)
            returnVal = jFileChooserXML.showOpenDialog(null);
        else
            returnVal = jFileChooserXML.showSaveDialog(null);
        if (returnVal == JFileChooser.ERROR_OPTION) {
            throw new ExceptionXML("Problem when opening file");
        }else if(returnVal == JFileChooser.CANCEL_OPTION ) {
            throw new ExceptionXML("Cancel opening file");
        }
        return new File(jFileChooserXML.getSelectedFile().getAbsolutePath());
    }

    /**
     * Filter files on file explorer to only allow XML files.
     * @param f the file to check
     * @return whether file is accepted or not
     */
    @Override
    public boolean accept(File f) {
        if (f == null) return false;
        if (f.isDirectory()) return true;
        String extension = getExtension(f);
        if (extension == null) return false;
        return extension.contentEquals("xml");
    }

    /**
     * Get FileFilter description.
     * @return description
     */
    @Override
    public String getDescription() {
        return "XML file";
    }

    /**
     * Get file extension of a given file.
     * @param f the file to extract the extension
     * @return extension of the input file
     */
    private String getExtension(File f) {
        String filename = f.getName();
        int i = filename.lastIndexOf('.');
        if (i>0 && i<filename.length()-1)
            return filename.substring(i+1).toLowerCase();
        return null;
    }
}
