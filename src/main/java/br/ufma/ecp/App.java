package br.ufma.ecp;

import org.apache.commons.cli.*;

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
public class App {

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

    private static void translateFile(File file, CodeWriter code) {

        String input = fromFile(file);
        Parser p = new Parser(input);
        while (p.hasMoreCommands()) {
            var command = p.nextCommand();
            switch (command.type) {

                // arithmetics
                case ADD:
                    code.writeArithmeticAdd();
                    break;

                case SUB:
                    code.writeArithmeticSub();
                    break;

                case NEG:
                    code.writeArithmeticNeg();
                    break;

                case NOT:
                    code.writeArithmeticNot();
                    break;

                case EQ:
                    code.writeArithmeticEq();
                    break;

                case LT:
                    code.writeArithmeticLt();
                    break;

                case GT:
                    code.writeArithmeticGt();
                    break;

                case AND:
                    code.writeArithmeticAnd();
                    break;

                case OR:
                    code.writeArithmeticOr();
                    break;

                case PUSH:
                    code.writePush(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;

                case POP:
                    code.writePop(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;

                case GOTO:
                    code.writeGoto(command.args.get(0));
                    break;

                case IF:
                    code.writeIf(command.args.get(0));
                    break;

                case LABEL:
                    code.writeLabel(command.args.get(0));
                    break;

                case RETURN:
                    code.writeReturn();
                    break;

                case CALL:
                    code.writeCall(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;

                case FUNCTION:
                    code.writeFunction(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;

                default:
                    System.out.println(command.type.toString() + " not implemented");
            }

        }

    }

    public static void main(String[] args) {

        var bootstrap = false;
        // define options
        Options options = new Options();

        Option configB = Option.builder("b").longOpt("bootstrap")
                .desc("Include bootstrap code").build();
        options.addOption(configB);

        Option configI = Option.builder("i").longOpt("input")
                .argName("input")
                .hasArg()
                .required(true)
                .desc("Input files to compiles").build();
        options.addOption(configI);

        // define parser
        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("b")) {
                System.out.println("Include boostrap");
                bootstrap = true;
            }

            if (cmd.hasOption("i")) {
                String opt_file = cmd.getOptionValue("input");

                File file = new File(opt_file);

                if (!file.exists()) {
                    System.err.println("The file doesn't exist.");
                    System.exit(1);
                }

                // we need to compile every file in the directory
                if (file.isDirectory()) {

                    var outputFileName = file.getAbsolutePath() + "/" + file.getName() + ".asm";
                    System.out.println(outputFileName);
                    CodeWriter code = new CodeWriter(outputFileName);
                    if (bootstrap)  code.writeInit();

                    for (File f : file.listFiles()) {
                        if (f.isFile() && f.getName().endsWith(".vm")) {

                            var inputFileName = f.getAbsolutePath();
                            var pos = inputFileName.indexOf('.');

                            System.out.println("compiling " + inputFileName);
                            translateFile(f, code);

                        }

                    }
                    code.save();
                    // we only compile the single file
                } else if (file.isFile()) {
                    if (!file.getName().endsWith(".vm")) {
                        System.err.println("Please provide a file name ending with .vm");
                        System.exit(1);
                    } else {
                        var inputFileName = file.getAbsolutePath();
                        var pos = inputFileName.indexOf('.');
                        var outputFileName = inputFileName.substring(0, pos) + ".asm";
                        CodeWriter code = new CodeWriter(outputFileName);
                        System.out.println("compiling " + inputFileName);
                        if (bootstrap)  code.writeInit();
                        translateFile(file, code);
                        code.save();
                    }
                }
            }
        } catch (ParseException e) {
            System.out.println(args[0]);
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }

    }

}
