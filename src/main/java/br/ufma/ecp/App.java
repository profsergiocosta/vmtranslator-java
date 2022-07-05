package br.ufma.ecp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
 
    private static void translateFile (String filename, CodeWriter code) {

    }

    private static String fromFile(File file) {        

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            String textoDoArquivo = new String(bytes, "UTF-8");
            return textoDoArquivo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    } 

    public static void main_( String[] args )
    {
        //String fname = "/home/sergio/Google Drive/Arquivados/nand2tetris/projects/07/StackArithmetic/SimpleAdd/SimpleAdd.vm";
        
        String fname = args[0];

        File file = new File(fname);

        String outputFileName = fname.substring(0, fname.lastIndexOf(".")) + ".asm";
        CodeWriter code = new CodeWriter(outputFileName);

        String input = fromFile(file);
        Parser p = new Parser(input);
        while (p.hasMoreCommands()) {
            var command = p.nextCommand();
            switch (command.type) {

                case ADD:
                    code.writeArithmeticAdd();
                    break;

                case SUB:
                    code.writeArithmeticSub();
                    break;

                case PUSH:
                    code.writePush(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;
                
                case POP:
                    code.writePop(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;

                default:
                    System.out.println(command.type.toString()+" not implemented");
            }

    
        } 
        
        //System.out.println(code.codeOutput());
        code.save();
        //CodeWriter code = new CodeWriter();
        //code.setFileName("/home/sergio/Google Drive/Arquivados/nand2tetris/projects/07/StackArithmetic/SimpleAdd/Add.vm");

    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide a single file path argument.");
            System.exit(1);
        }

        File file = new File(args[0]);

        if (!file.exists()) {
            System.err.println("The file doesn't exist.");
            System.exit(1);
        }

        // we need to compile every file in the directory
        if (file.isDirectory()) {

            var outputFileName = file.getAbsolutePath() +"/"+ file.getName()+".asm";
            System.out.println(outputFileName);

            for (File f : file.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".vm")) {

                    var inputFileName = f.getAbsolutePath();
                    var pos = inputFileName.indexOf('.');
                    
                    
                    System.out.println("compiling " +  inputFileName);
                    var input = fromFile(f);
                    //var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
                    //parser.parse();
                    ///var result = parser.VMOutput();
                  //  saveToFile(outputFileName, result);
                }

            }
        // we only compile the single file
        } else if (file.isFile()) {
            if (!file.getName().endsWith(".vm"))  {
                System.err.println("Please provide a file name ending with .vm");
                System.exit(1);
            } else {
                var inputFileName = file.getAbsolutePath();
                var pos = inputFileName.indexOf('.');
                var outputFileName = inputFileName.substring(0, pos) + ".vm";
                
                System.out.println("compiling ... " +  inputFileName);
                var input = fromFile(file);
               // var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
               // parser.parse();
                //var result = parser.VMOutput();
                //saveToFile(outputFileName, result);
                
            }
        }
    }

}
