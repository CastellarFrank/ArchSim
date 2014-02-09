package VerilogCompiler;  

import java_cup.runtime.*;
    
%%

%class VerilogLexer
%public

%line
%column
    

%cup
   

%{   
    
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
   
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   
LineTerminator = \r|\n|\r\n
   

WhiteSpace     = {LineTerminator} | [ \t\f]

sign           = [+-]

decimal_digit  = [0-9]
octal_digit    = [0-7xXzZ]
binary_digit   = [01xXzZ]
hex_digit      = [0-9a-fA-FxXzZ]

unsigned_number = {decimal_digit}({decimal_digit})*
size		= {unsigned_number}

decimal_base	= 'd|'D
octal_base	= 'o|'O
binary_base	= 'b|'B
hex_base	= 'h|'H

decimal_number	= {sign}{unsigned_number}|{size}{decimal_base}{unsigned_number}
octal_number	= {size}{octal_base}{octal_digit}(_|{octal_digit})*
binary_number	= {size}{binary_base}{binary_digit}(_|{binary_digit})*
hex_number	= {size}{hex_base}{hex_digit}(_|{hex_digit})*

hex_simple      = {hex_digit}(_|{hex_digit})*

number          = {decimal_number}|{octal_number}|{binary_number}|{hex_number}

letter          = [a-zA-Z]
identifier	= (_|{letter})(_|{letter}|{decimal_digit})*

printable_char	= [ -~]
line_comment	= [/][/][ -~]*(\r?\n)
block_comment	= [/][\*][ -~\n]*[\*][/]

level_symbol	= [01xX?]
edge_symbol	= [pn]
%%

   
<YYINITIAL> {
   
   
    ";"                { return symbol(sym.SEMICOLON); }
    ","                { return symbol(sym.COMMA); }
    "?"                { return symbol(sym.QUESTION_MARK); }
    "@"                { return symbol(sym.AT_SIGN); }
    ":"                { return symbol(sym.COLON); }
    "#"                { return symbol(sym.SHARP); }

    "+"                { return symbol(sym.OP_ADD); }
    "="                { return symbol(sym.EQUALS); }
    "-"                { return symbol(sym.OP_MINUS); }
    "*"                { return symbol(sym.OP_TIMES); }
    "/"                { return symbol(sym.OP_DIV); }
    "("                { return symbol(sym.L_PARENTHESIS); }
    ")"                { return symbol(sym.R_PARENTHESIS); }
    "{"                { return symbol(sym.L_CURLY); }
    "}"                { return symbol(sym.R_CURLY); }
    "["                { return symbol(sym.L_BRACKET); }
    "]"                { return symbol(sym.R_BRACKET); }
    "always"           { return symbol(sym.KW_ALWAYS); }
    "and"              { return symbol(sym.KW_AND); }
    "assign"           { return symbol(sym.KW_ASSIGN); }
    "begin"            { return symbol(sym.KW_BEGIN); }
    "case"             { return symbol(sym.KW_CASE); }
    "deassign"         { return symbol(sym.KW_DEASSIGN); }
    "default"          { return symbol(sym.KW_DEFAULT); }
    "edge"             { return symbol(sym.KW_EDGE); }
    "else"             { return symbol(sym.KW_ELSE); }
    "end"              { return symbol(sym.KW_END); }
    "endcase"          { return symbol(sym.KW_ENDCASE); }
    "endmodule"        { return symbol(sym.KW_ENDMODULE); }
    "for"              { return symbol(sym.KW_FOR); }
    "forever"          { return symbol(sym.KW_FOREVER); }
    "if"               { return symbol(sym.KW_IF); }
    "initial"          { return symbol(sym.KW_INITIAL); }
    "inout"            { return symbol(sym.KW_INOUT); }
    "integer"          { return symbol(sym.KW_INTEGER); }
    "input"            { return symbol(sym.KW_INPUT); }
    "module"           { return symbol(sym.KW_MODULE); }
    "nand"             { return symbol(sym.KW_NAND); }
    "negedge"          { return symbol(sym.KW_NEGEDGE); }
    "nor"              { return symbol(sym.KW_NOR); }
    "output"           { return symbol(sym.KW_OUTPUT); }
    "or"               { return symbol(sym.KW_OR); }
    "parameter"        { return symbol(sym.KW_PARAMETER); }
    "posedge"          { return symbol(sym.KW_POSEDGE); }
    "reg"              { return symbol(sym.KW_REG); }
    "repeat"           { return symbol(sym.KW_REPEAT); }
    "signed"           { return symbol(sym.KW_SIGNED); }
    "supply0"          { return symbol(sym.KW_SUPPLY0); }
    "supply1"          { return symbol(sym.KW_SUPPLY1); }
    "wand"             { return symbol(sym.KW_WAND); }
    "wor"              { return symbol(sym.KW_WOR); }
    "unsigned"         { return symbol(sym.KW_UNSIGNED); }
    "wait"             { return symbol(sym.KW_WAIT); }
    "while"            { return symbol(sym.KW_WHILE); }
    "wire"             { return symbol(sym.KW_WIRE); }
    "xnor"             { return symbol(sym.KW_XNOR); }
    "xor"              { return symbol(sym.KW_XOR); }

    "&&"               { return symbol(sym.LOG_AND); }
    "||"               { return symbol(sym.LOG_OR); }
    "=="               { return symbol(sym.REL_EQ); }
    "!="               { return symbol(sym.REL_NOTEQ); }
    ">"                { return symbol(sym.REL_GRT); }
    ">="               { return symbol(sym.REL_GRTEQ); }
    "<"                { return symbol(sym.REL_LST); }
    "<="               { return symbol(sym.REL_LSTEQ); }
    "!"                { return symbol(sym.LOG_NEG); }
    "<<"               { return symbol(sym.L_SHIFT); }
    ">>"               { return symbol(sym.R_SHIFT); }
    "<<<"              { return symbol(sym.L_ARIT_SHIFT); }
    ">>>"              { return symbol(sym.R_ARIT_SHIFT); }

    "&"               { return symbol(sym.BIT_AND); }
    "|"               { return symbol(sym.BIT_OR); }
    "~"               { return symbol(sym.BIT_NEG); }
    "^"               { return symbol(sym.BIT_XOR); }
    "~^"              { return symbol(sym.BIT_XNOR); }
    "^~"              { return symbol(sym.BIT_XNOR); }
   
    /* Don't do anything if whitespace is found */
    {WhiteSpace}       { /* just skip what was found, do nothing */ } 
    {line_comment}     { /* just skip what was found, do nothing */ }   
    {block_comment}    { /* just skip what was found, do nothing */ } 

    {identifier}         { return symbol(sym.IDENTIFIER, new String(yytext())); }

    {decimal_base}       { return symbol(sym.DECIMAL_BASE); }
    {octal_base}         { return symbol(sym.OCTAL_BASE); }
    {binary_base}        { return symbol(sym.BINARY_BASE); }
    {hex_base}           { return symbol(sym.HEX_BASE); }

    {unsigned_number}    { return symbol(sym.UNSIGNED_NUMBER, new Long(yytext())); }

    {hex_simple}         { return symbol(sym.HEX_NUMBER, new String(yytext())); }
}



[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
