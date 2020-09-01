import java.io.IOException;

public class Main {
  
  public static void main(String[] args) throws NoSuchMethodException,IOException {
  
    CRUD<Livro> Livraria = new CRUD<>(Livro.class.getConstructor(),"teste");
    
    Livro livro1 = new Livro("Livro1","Autor1",100);
    Livro livro2 = new Livro("Livro2","Autor2",50);
    Livro livro3 = new Livro("Livro3","Autor3",24);
    Livraria.create(livro1);
    Livraria.create(livro2);
    Livraria.create(livro3);
    
    System.out.println(Livraria.read(0));
    System.out.println(Livraria.read(1));

    Livraria.delete(2);

    if(Livraria.read(2) == null)
            System.out.println("Livro ID:2 apagado com sucesso!");

  }
}
