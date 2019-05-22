package com.ssplugins.shadow3.test;

import com.ssplugins.shadow3.ShadowCommons;
import com.ssplugins.shadow3.api.ShadowContext;
import com.ssplugins.shadow3.parsing.TokenLine;
import com.ssplugins.shadow3.parsing.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TEST {
    
    public static void main(String[] args) throws IOException {
        
        InputStream stream = TEST.class.getResourceAsStream("/com/ssplugins/shadow3/test/testy.shd");
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
    
        ShadowContext context = ShadowCommons.create();
    
        Tokenizer tokenizer = new Tokenizer();
        List<TokenLine> t = tokenizer.tokenize(lines, context);
        t.size();
    }
    
}
