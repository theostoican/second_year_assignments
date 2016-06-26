%include "io.inc"
extern printf
extern putchar

section .data
    %include "input.inc"
    printf_format db "%u",0
    printf_format_wrong db "Baza incorecta", 13, 10, 0
section .text
global CMAIN
CMAIN:
    mov ebp, esp; for correct debugging
    push ebp
    mov ebp, esp
    mov eax, [baza]
    cmp eax, 2
    jl end_wrong
    mov eax, [baza]
    cmp eax, 16
    jg end_wrong
    xor eax, eax
    xor edx, edx
    xor ecx, ecx
    push ecx
convert:
    pop ecx
    mov eax, [numar]
    xor edx, edx
    mov ebx, [baza]
    div ebx
    inc ecx
    ;updatez numarul, cu catul impartirii
    mov [numar], eax
    push edx
    push ecx
    cmp eax, 0
    jne convert
    pop ecx
numberCase:
    dec ecx
    cmp ecx, -1
    je end
    pop eax
    cmp eax, 9
    jg letterCase
    push ecx
    add eax, '0'
    push eax 
   ; push printf_format
    call putchar
    ;add esp,4
    pop eax
    pop ecx
    jmp numberCase
    jmp end
letterCase:
    ;in cazul letterCase, tot ce fac este sa adaug numarul obtinut
    ;codului ASCII aferent literei "'a'-10"
    add eax, 87
    push ecx
    push eax
    call putchar
    pop eax
    pop ecx
    ;ma intorc inapoi la celalalt label, pentru a putea continua 
    ;codificarea numarului
    jmp numberCase
end:
    leave
    ret
end_wrong:
    push printf_format_wrong
    call printf
    leave
    ret