%include "io.inc"
extern PRINT_UDEC
extern PRINT_CHAR
extern printf
extern putchar
extern malloc
section .data
   ; arrayMulDim: dd 0
   ; oddOrEven: db 0
    arrayMul: dd 0
section .text

global convert_to_native
global do_operation
global print_number

; TODO dissasemble the main.o file. Be sure not to overwrite registers used
; by main.o that he does not save himself.
; If you see your program going nuts consider looking in the main.o disassembly
; for the causes mentioned earlier.

convert_to_native:
    push ebp
    mov ebp, esp
    xor ecx, ecx
    push ecx
start:
    ;push ecx
    pop ecx
    ;PRINT_UDEC 4, ecx
    ;PRINT_CHAR 10
    ;PRINT_UDEC 4, 
    mov ebx, [ebp + 16 + ecx];ebx->adresa lui a
    mov al, byte[ebx]
    ;PRINT_UDEC 1, al
    ;PRINT_CHAR 10
    cmp al, 255
    je minus
    ;mov ecx, dword[eax+4]
    
    mov eax, [ebp+8 + ecx]; eax -> adresa lui a_str
    push ecx
minusOrPlus:
    xor ecx, ecx
    ;mov ecx, dword[ebx+4]; ecx -> lungimea lui a
    push ebx
loop:
    mov bl, byte[eax+ecx]
    ;PRINT_UDEC 1, bl
    ;PRINT_CHAR 10
    inc ecx
    cmp bl, 0
    jne loop
    
    dec ecx
    ;de la adresa ebx+8, e stocat numarul in a
    pop ebx
    mov edx, ecx
    push eax
    push edx
    xor eax, eax
    mov eax, edx
    xor edx, edx
    mov dl, 2
    div dl
    xor esi, esi
    mov esi, eax
    pop edx
    pop eax
    push esi
    push eax
    push edx
    mov eax, esi
    cmp ah, 1
    je plusEven
    pop edx
    pop eax
    mov ecx, dword[ebx+4]
    mov edx, ecx
plus:
    mov edi, edx
    sub edi, ecx
    push eax
    push ebx
    push ecx
    push edx
    xor ebx, ebx
    xor edx, edx
    mov bh, byte[eax + 2*edi]
    mov dh, byte[eax + 1 + 2*edi]
    cmp dh, 65
    jge CONVEDI
    sub dh, 48
    jmp continue1
plusEven:
    pop edx
    pop eax
    mov ecx, dword[ebx+4]
    mov edx, ecx
repeatEven:
    mov edi, edx
    sub edi, ecx
    push eax
    push ebx
    push ecx
    push edx
    xor ebx, ebx
    xor edx, edx
    cmp edi,0
    je firstByteEven
    mov bh, byte[eax+2*edi-1]
    mov dh, byte[eax+2*edi]
afterFirstByteEven:
    cmp dh, 65
    jge CONVEDI
    sub dh, 48
continue1:
    cmp bh, 65
    jge CONVESI
    cmp bh, 48
    jge CONVESI2
continue2:
    xor eax, eax
    mov al, bh
    xor ecx, ecx
    mov ch, 16
    mul ch
    add al, dh
    pop edx
    pop ecx
    pop ebx
    mov byte[ebx+8+ecx-1], al
    pop eax
    cmp ecx, 1
    je final
    dec ecx
    pop esi
    push eax
    mov eax, esi
    cmp ah, 1
    je jumpToEven
    pop eax
    push esi
    cmp ecx, 1
    jge plus
final:
    pop esi
    pop ecx
        ;PRINT_UDEC 4, ecx
    
    
    mov eax, [ebp+16]
    mov bl, byte[eax+8]
    ;PRINT_UDEC 1, bl
    ;PRINT_CHAR 10
    
    
    cmp cl, 4
    jne addAndStartForB
     ;   PRINT_CHAR 'F'
    ;push ecx
    ;push ecx
    leave 
    ret
addAndStartForB:
    xor ecx, ecx
    mov cl, 4
    push ecx
    jmp start
firstByteEven:
    mov bh, 48
    mov dh, byte[eax+2*edi]
    jmp afterFirstByteEven
CONVEDI:
    sub dh, 55
    jmp continue1
CONVESI:
    sub bh, 55
    jmp continue2
CONVESI2:
    sub bh, 48
    jmp continue2
jumpToEven:
    pop eax
    push esi
    cmp ecx, 1
    jge repeatEven
minus:
    ;PRINT_CHAR 'M'
    mov eax, [ebp+8+ecx]
    add eax, 1
    push ecx
    jmp minusOrPlus    
do_operation:
    ; TODO
    push ebp
    mov ebp, esp
    
    mov eax, [ebp+16]
    mov bl, byte[eax]
    ;PRINT_UDEC 1, bl
    ;PRINT_CHAR 10
    ;PRINT_CHAR 10
    cmp bl, 43
    je add
    cmp bl, 60
    je shiftLeft
    cmp bl, 62
    je shiftRight
    cmp bl, 49
    je multiplication
    
multiplication:
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    mov esi, [eax+4]
    mov edi, [ebx+4]
    mov edx, esi
    add edx, edi; ->dimensiunea rezultatului
    ;mov dword[arrayMulDim], edx
    push edx
    push edx
    ;PRINT_UDEC 4, edx
    ;PRINT_CHAR 10
    ;PRINT_CHAR 10
    call malloc
    mov dword[arrayMul], eax
    add esp, 4
    ;mov edx, 4
    ;push edx
    ;call malloc
    ;mov dword[arrayMulDim], eax
    ;add esp, 4
    pop edx
    mov ecx, edx
    push edx
     ;      PRINT_CHAR 'B'
    ;PRINT_UDEC 4, ecx
    ;PRINT_CHAR 10
    xor esi, esi
    loopZeroArray:
        mov dl, byte[arrayMul+esi]
        mov dl, 0
        mov byte[arrayMul+esi], dl
       ; PRINT_UDEC 1, dl
        ;PRINT_CHAR 32
        inc esi
        cmp esi, ecx
        jl loopZeroArray
   ; PRINT_CHAR 10
    mov ecx, 0
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    xor ecx, ecx
    loopMul1:
        mov dl, byte[eax+8+ecx];->byte din primul numar
        push eax
        push ebx
        push ecx
        mov esi, ecx
        xor ecx, ecx
        ;PRINT_CHAR 10
        loopMul2:
            ;xor edx, edx
            mov al, byte[ebx+8+ecx]
            ;PRINT_UDEC 1, al
            ;PRINT_CHAR 10
            ;PRINT_CHAR 10
            mul dl
           ; PRINT_CHAR 'A'
           ; PRINT_UDEC 2, ax
           ; PRINT_CHAR 10
           ; PRINT_CHAR 10
            push edx
            push ecx
            push ebx
            loopAdditionCarry:
                mov dl, byte[arrayMul+esi+ecx]
                ;mov dh, dl
                add dl, al
                ;PRINT_UDEC 1, al
                ;PRINT_CHAR 10
                mov byte[arrayMul+esi + ecx], dl
                mov al, 0
                adc al, 0
                mov bl, byte[arrayMul + esi + ecx]
                inc ecx
                cmp al, 0
                jne loopAdditionCarry
            pop ebx
            pop ecx
            push ecx
            push ebx
            mov edi, ecx
            inc edi
            ;ah trebuie sa fie intact
            loopMultiplicationCarry:
                mov al, byte[arrayMul+esi+edi]
                add al, ah
                mov ah, 0
                adc ah, 0
                mov dl, byte[arrayMul+esi+edi]
                mov byte[arrayMul+esi+edi], al
                inc edi
                cmp ah, 0
                jne loopMultiplicationCarry
            ;PRINT_CHAR 10
            mov ecx, 0
            loopPrint:
                mov al, byte[arrayMul+ecx]
               ; PRINT_UDEC 1, al
                ;PRINT_CHAR 10
                inc ecx
                cmp ecx, 6
                jl loopPrint
           ; PRINT_CHAR 10
            pop ebx
            pop ecx
            pop edx
            inc ecx
            mov edi, [ebx+4]
            cmp ecx , edi
            jl loopMul2
        pop ecx
        pop ebx
        pop eax
        mov edx, [eax+4]
        inc ecx
        cmp ecx, edx
        jl loopMul1 
    pop edx
    mov ecx, edx
    ;PRINT_CHAR 'Z'
    ;PRINT_UDEC 4, ecx
    ;PRINT_CHAR 10
    mov eax, [ebp+8]
    xor esi, esi
    loopCopyResult:
        ;PRINT_UDEC 4, ecx
        ;PRINT_CHAR 10
        mov dl, byte[arrayMul+esi]
        mov byte[eax+8+esi], dl
        inc esi
        cmp esi, ecx
        jl loopCopyResult
    mov esi, ecx
    loopResize:
        mov dl, byte[eax+8+esi-1]
        cmp dl, 0
        jne notResize
        dec ecx
        dec esi
        cmp esi, 1
        jge loopResize
    notResize:
    mov dword[eax+4], ecx
    mov ebx, [ebp+12]
    mov dl, byte[eax]
    cmp dl, 0
    jne plusMulContinue
    mov esi, dword[ebx]
    mov dword[eax], esi
    jmp minusMulContinue
plusMulContinue:
    mov dl, byte[ebx]
    cmp dl, 255
    jne minusMulContinue
    mov edx, 0
    mov dword[eax], edx
minusMulContinue:
    leave 
    ret
    
    
    
add:
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    ;mov edx, [eax+8]
    ;PRINT_CHAR 'T'
    ;PRINT_UDEC 4, edx
    ;PRINT_CHAR 10
    mov ecx, dword[eax+4]
    mov edx, dword[ebx+4]
    cmp ecx, edx
    jl change
    
    je findMaxNumber
    
    push eax
continueAfterChange:
    ;pop ecx
    ;mov al, byte[ecx]
    ;mov eax, dword[eax]
    ;PRINT_UDEC 1, al
    ;PRINT_CHAR 10
    ;PRINT_CHAR 'A'
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    mov cl, byte[eax]
    mov ch, byte[ebx]
    cmp cl, 255
    je complementA
continueComplementA:
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    mov ch, byte[ebx]
    cmp ch, 255
    je complementB
continueComplementB:
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    ;am terminat cu complementarea aici
    ;facem padding pe numere in functie de dimensiuni
    mov dl, [eax]
    cmp dl, 255
    je paddingATest
continuePaddingA:
    mov dl, [ebx]
    cmp dl, 255
    je paddingBTest
continuePaddingB:
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
  
    mov edx, dword[ebx+8]
    ;PRINT_UDEC 4, edx
    ;PRINT_CHAR 10
    
    ;mov edx, [eax+8]
   ;PRINT_CHAR 'T'
    ;PRINT_UDEC 4, edx
    ;PRINT_CHAR 10
    mov esi, dword[eax+8]
    mov edi, dword[ebx+8]     
    pop ecx
    push ecx
    mov ecx, [ecx+4]
    inc ecx
    mov esi, ecx
    sub ecx, esi
    sub ecx, esi
    xor edx, edx
    mov edi, 8
    add edi, esi
    clc
    loopEffectiveAdd:
        ;mov edi, esi
        ;sub edi, ecx
        ;mov edi, esi
        ;add edi, ecx
        mov esi, edi
        add esi, ecx
        clc             
        mov dl, byte[eax+esi]      
        add dl, byte[ebx+esi]
        mov byte[eax+esi], dl
        inc ecx
        mov dl, byte[eax+esi+1]
        adc dl, 0
        mov byte[eax+esi+1], dl
        ;cmp ecx, esi
        jnz loopEffectiveAdd
    ;Stabilesc dimensiunea fara sa complementez
    ;mov edi, esi
    ;sub edi, ecx
    ;mov dl, byte[eax+8+edi]
    ;adc dl, 0
    ;mov byte[eax+8+edi], dl
    sub edi, 8    
    mov dl, byte[eax+esi+1]
    cmp dl, 0
    jne biggerDim
    dec edi
biggerDim:
    mov dword[eax+4], edi
    pop edx
    mov ebx, [edx]
    mov [eax], ebx
    mov cl, byte[edx]
    ;PRINT_UDEC 1, cl
    ;PRINT_CHAR 10   
    cmp cl, 255
    je complementResult
    ;PRINT_CHAR 10
    ;pop ecx
    ;mov [eax+4], ecx
obtainedFinalResult:
    leave 
    ret
complementResult:
    mov esi, [edx+4]
    push edx
    inc esi
    mov eax, [ebp+8]
    mov cl, [eax+8+esi-1]
    cmp cl, 0
    jne overflowComplement
    dec esi
overflowComplementContinue:
    inc esi
    xor edx, edx
    mov eax, [ebp+8]
    mov ecx, esi
    mov bl, 1
    clc
    loopComplementResult:
        mov edi, esi
        sub edi, ecx
        mov dl, byte[eax+8+edi]
        not dl
        adc dl, bl
        xor ebx, ebx
        mov byte[eax+8+edi], dl
        dec ecx
        jnz loopComplementResult
    ;mov dword[eax+4], esi
    pop edx
    mov edx, [edx]
   ; mov dword[eax], edx
    jmp obtainedFinalResult 
overflowComplement:
    mov cl, 255
    mov byte[eax+8+esi-1], cl
    jmp overflowComplementContinue
paddingBTest:
    mov ecx, [eax+4]
    mov edx, [ebx+4]
    cmp ecx, edx
    jg paddingB
    ;PRINT_CHAR 'B'
    ;PRINT_CHAR 10
    jmp continuePaddingB
paddingB:
    mov ecx, [ebx+4]
    forPaddingB:
        mov dl, [ebx+8+ecx]
        not dl
        ;PRINT_UDEC 1, dl
        ;PRINT_CHAR 10
        mov [ebx+8+ecx], dl
        inc ecx
        mov edx, [eax+4]
        dec edx
        cmp ecx, edx
        jle forPaddingB
        jmp continuePaddingB
paddingATest:
    mov ecx, [eax+4]
    mov edx, [ebx+4]
    cmp ecx, edx
    jl paddingA
    jmp continuePaddingA
paddingA:
    mov ecx, [eax+4]
    forPaddingA:
         mov dl, [eax+8+ecx]
         not dl
         mov [eax+8+ecx], dl
         inc ecx
         mov edx, [ebx+4]
         dec edx
         cmp ecx, edx
         jle forPaddingA
         jmp continuePaddingB
complementA:
    ;PRINT_CHAR 'B'
    mov esi, dword[eax+4];->lungimea lui a
    mov ecx, esi
    xor ebx, ebx
    mov bl, 1
    clc
    forComplementA:
        mov edi, esi
        sub edi, ecx
        mov dl, byte[eax+8+edi]        
        not dl
        adc dl, bl
        xor ebx, ebx
        mov byte[eax+8+edi], dl
        dec ecx
        jnz forComplementA
        jmp continueComplementA
complementB:
    ;PRINT_CHAR 'A'
    mov ebx, [ebp+12];->adresa lui b
    mov esi, dword[ebx+4];->dimensiunea lui b
    mov ecx, esi
    xor eax, eax
    mov al, 1
    clc
    forComplementB:
        mov edi, esi
        sub edi, ecx
        mov dl, byte[ebx+8+edi]
        not dl
        adc dl, al
        ;PRINT_UDEC 1, dl
        ;PRINT_CHAR 10
        xor eax, eax
        mov byte[ebx+8+edi], dl
        dec ecx
        jnz forComplementB
        jmp continueComplementB
change:
    mov ecx, edx
    push ebx
    jmp continueAfterChange   
findMaxNumber:
    mov eax,[ebp+8]
    mov ebx,[ebp+12]
    mov ecx, [eax+4]
    ;----------------MODIFICARE NOUA
    dec ecx
    ;PRINT_CHAR 'T'
    loopMaxNumber:
        ;PRINT_CHAR 'M'
        ;PRINT_UDEC 4, ecx
        ;PRINT_CHAR 10    
        cmp ecx, -1
        je endLoopMaxNumber
        
        mov dh, byte[ebx+8+ecx]
        mov dl, byte[eax+8+ecx]
        ;PRINT_CHAR 10
        ;PRINT_UDEC 1, dl
        ;PRINT_CHAR 10
        ;PRINT_UDEC 1, dh
        ;PRINT_CHAR 10

        dec ecx

        cmp dl, dh
        ja eaxBigger
        ;PRINT_CHAR 10
        ;PRINT_UDEC 1, dl
        ;PRINT_CHAR 10
        ;PRINT_UDEC 1, dh
        ;PRINT_CHAR 10
        cmp dl, dh
        jb ebxBigger
        cmp dl, dh
        je loopMaxNumber
    endLoopMaxNumber:
        mov cl, byte[eax]
        ;PRINT_UDEC 1, cl
        cmp cl, 255
        je makePositive
        push eax
        jmp continueAfterChange
    makePositive:
        push ebx
        jmp continueAfterChange
    eaxBigger:
        ;PRINT_CHAR 'M'
        push eax
        jmp continueAfterChange
    ebxBigger:
        ;PRINT_CHAR 'A'
        push ebx
        jmp continueAfterChange
 
     
shiftRight:
    mov eax, [ebp+8];->adresa nr de shiftat
    mov ebx, [ebp+12]
    xor ecx, ecx
    mov ecx, dword[ebx+8];->cat trebuie sa shiftez
    
    loopShiftRight:
        push ecx
        mov ecx, [eax+4]
        xor edx, edx
        forShiftRight:
            mov bl, byte[eax+8+ecx-1]
            shr bl, 1
            adc dl, 0
            add bl, dh
            push eax
            xor eax, eax
            mov al, dl
            mov esi, 128
            mul esi
            mov dl, al
            pop eax
            mov dh, dl
            ;PRINT_UDEC 1, dh
            ;PRINT_CHAR 10
            ;PRINT_CHAR 10
            mov dl, 0
            mov byte[eax+8+ecx-1], bl
            dec ecx
            jnz forShiftRight
        push eax
        mov ecx, [eax+4]
        mov al, byte[eax+8+ecx-1]
        cmp al, 0
        jne noReduceDimension
        pop eax
        push eax
        mov ecx, [eax+4]
        dec ecx
        mov [eax+4], ecx
    noReduceDimension:
        pop eax
        pop ecx
        dec cl
        jnz loopShiftRight
leave 
ret
            
           
                 
shiftLeft:
    mov eax, [ebp+8];->adresa numarului de shiftat
    mov ebx, [ebp+12]
    xor ecx, ecx
    mov ecx, dword[ebx+8];->cat trebuie sa shiftez
    ;PRINT_UDEC 4, ecx
    ;PRINT_CHAR 10
    ;leave 
    ;ret
loopShiftLeft:
    push ecx
    mov ecx, [eax+4];->lungimea numarului
    mov edi, ecx
    xor esi, esi
    xor edx, edx
    clc
    forShift:
        mov esi, edi
        sub esi, ecx
        mov bl, byte[eax+8+esi]
        shl bl, 1
        adc dl, 0
        add bl, dh
        mov dh, dl
        mov dl, 0
        mov byte[eax+8+esi], bl
        dec ecx
        jnz forShift
    cmp dh, 0
    jne incDimension
    incDimensionContinue:
    pop ecx
    ;PRINT_UDEC 4, ecx
    ;PRINT_CHAR 10
    dec cl
    ;PRINT_UDEC 4, ecx
    ;PRINT_CHAR 10
    ;PRINT_CHAR 10
    jnz loopShiftLeft
leave 
ret
    
incDimension:
    mov ecx, [eax+4]
    inc ecx
    mov [eax+4], ecx
    mov bl, byte[eax+8+ecx-1]
    add bl, dh
    mov byte[eax+8+ecx-1], bl
    jmp incDimensionContinue
         
        

        
                
                        
                                
                                        
                                                
                                                        
                                                                
                                                                        
                                                                                
                                                                                        
                                                                                                
                                                                                                                
print_number:
    push ebp 
    mov ebp, esp
    mov ebx, [ebp+8]
    mov dl, [ebx]
    cmp dl, 255
    je printMinus
    ;PRINT_CHAR '+'
    jmp afterPrintMinus
printMinus:
    xor ecx, ecx
    mov cl, 45
    push ecx
    call putchar
    add esp, 4
    ;PRINT_CHAR '-'
afterPrintMinus:
    mov ecx, [ebx+4]
    xor edx, edx
    loopConvert:
        push ebx
        push ecx
        xor eax, eax
        mov al, byte[ebx+8+ecx-1]
        mov ebx, 16
        ;PRINT_UDEC 4, eax
        ;PRINT_CHAR 10
        xor esi, esi
        xor edx, edx
        loopDivide:
            div ebx
            ;in al - catul si in dl - restul
            ;PRINT_UDEC 1, dl
            ;PRINT_CHAR 10
            ;xor edx, edx
            ;mov dl, ah
            ;PRINT_CHAR 'A'
            ;PRINT_UDEC 1, al
            ;PRINT_CHAR 10
            push edx
            inc esi
            cmp al, 0
            jne loopDivide
        push esi
        mov eax, esi
        xor edx, edx
        mov esi, 2
        ;PRINT_UDEC 4, esi
        div esi
        cmp dl, 0
        je noPadding
        mov edi, [ebp+8]
        mov edi, [edi+4]
        cmp edi, ecx
        je noPadding
        mov eax, 0
        pop esi
        inc esi
        push eax
        jmp loopPrintHex
        noPadding:
        pop esi
        loopPrintHex:
            pop edx
            cmp dl, 10
            jb printNumber
            add dl, 55
            xor eax, eax
            mov al, dl
            xor edx, edx
            mov dl, al
            push esi
            push edx
            call putchar
            add esp, 4
            pop esi
            jmp continueConvert
            printNumber:
            mov al, dl
            xor edx, edx
            mov dl, al
            add dl, '0'
            push edx
            call putchar
            add esp, 4
            ;PRINT_UDEC 1, dl
            ;PRINT_CHAR 10
            continueConvert:
            dec esi
            cmp esi, 0
            jne loopPrintHex
        pop ecx
        pop ebx
        dec ecx
        jnz loopConvert
    leave
    ret