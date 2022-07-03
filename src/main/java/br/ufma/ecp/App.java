package br.ufma.ecp;

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
 
    public static void main( String[] args )
    {
          String input =  """
            // This file is part of www.nand2tetris.org
            // and the book "The Elements of Computing Systems"
            // by Nisan and Schocken, MIT Press.
            // File name: projects/07/MemoryAccess/BasicTest/BasicTest.vm
            


            // Executes pop and push commands using the virtual memory segments.
            push constant 10
            pop local 0
            push constant 21
            push constant 22
            pop argument 2
            pop argument 1
            push constant 36
            pop this 6
            push constant 42
            push constant 45
            pop that 5
            pop that 2
            push constant 510
            pop temp 6
            push local 0
            push that 5
            add
            push argument 1
            sub
            push this 6
            push this 6
            add
            sub
            push temp 6
            add
         """;

         input = "push argument 3";
     
        CodeWriter code = new CodeWriter();
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
                    code.writePush(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;

                default:
                    System.out.println(command.type.toString()+" not implemented");
            }

    
        } 
        
        System.out.println(code.codeOutput());
        //CodeWriter code = new CodeWriter();
        //code.setFileName("/home/sergio/Google Drive/Arquivados/nand2tetris/projects/07/StackArithmetic/SimpleAdd/Add.vm");

      
        

    

    }
}
