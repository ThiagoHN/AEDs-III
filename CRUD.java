import java.io.*;
import java.lang.reflect.*;

public class CRUD <T extends Registro> {

    private RandomAccessFile arquivo; 
    private Constructor<T> construtor;

    // Montar um CRUD
    public CRUD(Constructor<T> construtor, String nomeArquivo) throws IOException { 
        File dados = new File("dados");
        if(!dados.exists())
            dados.mkdir();

        this.construtor = construtor;
        arquivo = new RandomAccessFile(nomeArquivo + ".db", "rw");

        if(arquivo.length() <= 0)
            arquivo.writeInt(0);
    }

    // Metodo que cria novos objetos do CRUD
    public int create(T entidade) {
        int id = -1;

        try {
            
            //Indo no comeco do arquivo para pegar o id
            arquivo.seek(0);
            id = arquivo.readInt();
            entidade.setID(id);
            
            // Montando e separando o tamanho do objeto
            byte[] data = entidade.toByteArray();
            long tamanho = arquivo.length();

            // Indo para o final do arquivo
            arquivo.seek(tamanho);
            // Escrevendo a Lapide do arquivo
            arquivo.writeChar(' ');
            // Escrevendo o tamanho da entidade
            arquivo.writeInt(data.length);
            // Escrevendo os dados da entidade
            arquivo.write(data);

            // Sobrescrevendo o id antigo
            arquivo.seek(0);
            arquivo.writeInt(id+1);
            
        } catch (IOException error) {
            id = -1;
            System.err.print("Não vai dar não");
        }
        
        return id;
    }
 
    // Metodo de busca por objeto a partir do id fornecido
    public T read(int id) {
        T entidade = null;
        boolean sucesso = false;
        boolean lapide;

        try {
            // Indo para o primeiro registro, passando pelos Metados do arquivo
            arquivo.seek(4);

            // Busca sequencial dentro do arquivo
            while(arquivo.getFilePointer() < arquivo.length() && !sucesso) {
                lapide = false;
                    
                    // Verifica se o registro foi deletado ou não
                    if(arquivo.readChar() == '*') 
                        lapide = true;

                    int tamanho = arquivo.readInt();

                    /*
                    // Caso o registro foi deletado passa para o proximo registro
                    // Se não, cria uma entidade com as informações do registro 
                    */

                    if(lapide){
                        arquivo.seek(arquivo.getFilePointer() + tamanho);
                    } else {
                        byte[] data = new byte[tamanho];
                        arquivo.read(data);
                        entidade = this.construtor.newInstance();
                        entidade.fromByteArray(data);

                        // Verifica se o id de busca eh igual ao id da entidade
                        if(entidade.getID() == id) { 
                            sucesso = true;
                        }

                    }
            }           

        } catch (Exception errleitura) {
            System.err.println("Opa amigo, erro na leitura");
        }

        // Caso a entidade nao for encontrada dentro do arquivo
        if(!sucesso) entidade = null;
        
        return entidade;
    }
    
    // Metodo que apaga uma entidade do arquivo
    public boolean delete(int id) {
        T entidade = null;
        boolean sucesso = false;
        boolean lapide;
        long pos_Lapide = -1;

        try {
            // Indo para o primeiro registro, passando pelos Metados do arquivo
            arquivo.seek(4);

            while(arquivo.getFilePointer() < arquivo.length() && sucesso == false) 
            {
                lapide = false;
                pos_Lapide = arquivo.getFilePointer();
                    
                    // Verifica se o registro foi deletado ou não
                    if(arquivo.readChar() == '*') 
                        lapide = true;

                    int tamanho = arquivo.readInt();
          
                    /*
                    // Caso o registro foi deletado passa para o proximo registro
                    // Se não, cria uma entidade com as informações do registro 
                    */

                    if(lapide){
                        arquivo.seek(arquivo.getFilePointer() + tamanho);
                    } else {
                        byte[] data = new byte[tamanho];
                        arquivo.read(data);
                        entidade = this.construtor.newInstance();
                        entidade.fromByteArray(data);
                        
                        // Verifica se o id de busca eh igual ao id da entidade
                        if(entidade.getID() == id) {
                            sucesso = true;
                            arquivo.seek(pos_Lapide);
                            arquivo.writeChar('*');
                        }

                    }
            }           

        } catch (Exception errdeletar) {
            System.err.println("Opa amigo, erro ao deletar");
        }

        return sucesso;
    }
}