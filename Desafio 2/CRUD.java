import java.io.*;
import java.lang.reflect.*;

public class CRUD <T extends Registro> {

    private RandomAccessFile arquivo; 
    private Constructor<T> construtor;

    // Indices Direto e Indireto, armazenados em Hash Extensicel e ArvoreB+ respectivamente
    private HashExtensivel he;
    private ArvoreBMais_String_Int ab;

    // Montar um CRUD
    public CRUD(Constructor<T> construtor, String nomeArquivo) throws Exception { 
        File dados = new File("dados");
        if(!dados.exists())
            dados.mkdir();

        this.construtor = construtor;
        arquivo = new RandomAccessFile(nomeArquivo + ".db", "rw");

        if(arquivo.length() <= 0)
            arquivo.writeInt(0);

        // Cria os Indices, respectivos para o Hash e a ArvoreB++
        he = new HashExtensivel(10, "dados/" + nomeArquivo + "IndiceDireto" + ".dados", "dados/" + nomeArquivo + "cestos" + ".dados");
        ab = new ArvoreBMais_String_Int(10, "dados/" + nomeArquivo + "IndiceIndireto" + ".dados");


    }

    // Metodo que cria novos objetos do CRUD
    public int create(T entidade) throws Exception {
        int id = -1;
        long tamanho;

        try {
            
            //Indo no comeco do arquivo para pegar o id
            arquivo.seek(0);
            id = arquivo.readInt();
            entidade.setID(id);
            
            // Montando e separando o tamanho do objeto
            byte[] data = entidade.toByteArray();
            tamanho = arquivo.length();

            // Indo para o final do arquivo
            arquivo.seek(tamanho);

            // Crinado os registros na Hash Extensivel e na ArvoreB+
            he.create(id, tamanho + 2);
            ab.create(entidade.chaveSecundaria(), id);

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
        boolean sucesso = true;
        long pos = -1;
        int tamanho;

        try {
    
            pos = he.read(id);

            if (pos < 0)
            {
                System.err("Não foi possivel completar esta ação");
                sucesso = false;
            }

            arquivo.seek(pos);
            tamanho = arquivo.readInt();
            byte[] data = new byte[tamanho];
            arquivo.read(data);

			entidade = construtor.newInstance();
			entidade.fromByteArray(data);

        } catch (Exception errleitura) {
            System.err.println("Opa amigo, erro na leitura");
        }

        // Caso a entidade nao for encontrada dentro do arquivo
        if(!sucesso) entidade = null;
        
        return entidade;
    }

    // Metodo de busca por objeto a partir da chave secundaria
    public T read(String keySec) {
        T entidade = null;
        long tamanho = -1;

        try {
            
            tamanho = ab.read(keySec);          

        } catch (Exception errleitura) {
            System.err.println("Opa amigo, erro na leitura");
        }

        // Caso a entidade nao for encontrada dentro do arquivo
        if(tamanho >= 0) 
        {
            entidade = read((int)tamanho);
        }

        return entidade;
    }
    
    // Metodo que apaga uma entidade do arquivo
    public boolean delete(int id) {
        T entidade = null;
        boolean sucesso = false;
        long tamanho = -1;

        try {

            tamanho = he.read(id) - 2;

            entidade = read(id);
            he.delete(id);
            ab.delete(entidade.chaveSecundaria());

            arquivo.seek(tamanho);
            arquivo.writeChar('*');
            
        } catch (Exception errdeletar) {
            System.err.println("Opa amigo, erro ao deletar");
        }

        return sucesso;
    }
}