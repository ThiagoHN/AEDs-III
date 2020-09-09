import java.io.*;

public class Livro implements Registro 
{       
    protected int idEntidade;
    private String titulo;
    private String autor;
    private float preco;
    
    // Construtor do Livro
    public Livro(String titulo, String autor, float preco) 
    {
        setTitulo(titulo);
        setAutor(autor);
        setPreco(preco);
    }
    
    // Construtor do Livro vazio
    public Livro()
    {
        titulo = "unknow";
        autor = "unknow";
        preco = 0;
    }

    // Chave Secundaria de Pesquisa
    public String chaveSecundaria()
    {
        return this.titulo;
    }
    
    // Retorna o id da entidade
    public int getID()
    {
        return idEntidade;
    } 

    // Retorna o titulo da entidade
    public String getTitulo()
    {
        return titulo;
    }

    // Retorna o autor da entidade
    public String getAutor()
    {
        return autor;
    }

    // Retorna o preco da entidade
    public float getPreco()
    {
        return preco;
    }

    // Grava o valor do id
    public void setID(int id)
    {
        idEntidade = id;
    }

    // Gravar o titulo
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    // Gravar o autor
    public void setAutor(String autor)
    {
        this.autor = autor;
    }

    // Gravar o preco
    public void setPreco(float preco)
    {
        this.preco = preco;
    }

    // Serializar objeto
    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
    
        dos.writeInt(idEntidade);
        dos.writeUTF(titulo);
        dos.writeUTF(autor);
        dos.writeFloat(preco);
    
        return baos.toByteArray();
    }
      
    // Desserializar objeto
    public void fromByteArray(byte[] ba) throws IOException
    {  
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        setID(dis.readInt());
        setTitulo(dis.readUTF());
        setAutor(dis.readUTF());
        setPreco(dis.readFloat());
    }
    
    // Imprimi a entidade
    public String toString()
    {
        String resultado =  "Titulo: " + titulo + "\n" +
                            "Autor: " + autor + "\n" +
                            "Preco: R$ " + preco + "\n";

        return resultado;
    }

}