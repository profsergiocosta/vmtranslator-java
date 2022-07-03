package br.ufma.ecp;

import java.util.ArrayList;
import java.util.List;

public class Command {

    public enum Type {
        ADD, 
        SUB,
        NEG,
        EQ,
        GT,
        LT,
        AND,
        OR,
        NOT,
        PUSH,
        POP;
    }

    public Command.Type type;
    public List<String> args = new ArrayList<>();

    public Command (String[] command) {
        type = Command.Type.valueOf(command[0].toUpperCase());
        for (int i=1;i<command.length;i++){
            args.add(command[i]);
        } 
    }
    
}
