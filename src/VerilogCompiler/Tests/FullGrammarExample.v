module Full ( output reg x, input array[5], input myrange[2:10], inout otherArray[2], input y);
    /*parameters*/
    parameter x = 10, y = 20;
    parameter otherArray = 0;

    /*inputs*/
    input [0:7] display, number8bits;
    input noArray;
    input var1, var2, var3;

    //outputs
    output [1:8] test, Test;
    output oneVar;
    output var1, var2, var3;

    //inouts
    inout [2:9] noTest;
    inout variable;
    inout i, love, clean, code;

    //regs
    reg [0:2] reg1;
    reg justOneReg;
    reg listReg1, list2;
    reg [2:1] regs, regs2;
    reg [2:1] regs3[2:1];
    reg [3:0] regs4[0:0], otroReg;

    //nets
    wand var1, var2, var3;
    wor [3:5] var4, var5;
    wire [0:1] var6, var7;
    supply0 vcc;
    supply1 gnd;
    
    //gates
    and(1,2,3);
    and named(x, 1, x+y);
    or or_named(3);
    nand(array);

    //module instances
    addbit u0(r1[0],r2[0],ci,result[0],c1), u1();

    //assigns
    assign x = 3;
    assign {x,y,z} = 3'b000, x[1] = 3, array[1:2] = 1'hFFF;
    
    wire {x,y,z} = 3'b000, x[1] = 3, array[1:2] = 1'hFFF;

    initial @(*)
    begin
        x = 10;
        {x,0} <= 3'd10;
        if (y<z)
            array[0] = 0;

        if (z)
            tttt = 1;
        else
        begin
            
        end

        case(1)
            1'b000: x=1;
            2'b000: t=2;
            15: 
            begin
                repeat (x<1) x=1;
                forever (x=1;)
                x=~x;
                x <= x+1;
            end
            default: t = 3;
            default:
            begin
                while (x==x && y<=y || !z)
                begin
                    for(x=1;x<y;x=x+1)
                        ttt = 10;
                    assign ttt = ttt/10'b100;
                end
                wait (12'o20) ttt = 0;
            end
        endcase
    end

    always @(posedge a, negedge x, a or b)
    begin
    end

endmodule