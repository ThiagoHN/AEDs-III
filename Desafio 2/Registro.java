import java.io.IOException;

public interface Registro
{ 
    public String chaveSecundaria();
    public int getID();
    public void setID(int id);
    public byte[] toByteArray() throws IOException;
    public void fromByteArray (byte[] ba) throws IOException;
}