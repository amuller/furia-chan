// rules used to transform assembly code instructions to java bytecode 

// this is added here so that parameters respect this :)
// mem is the memory array
//type Reg64 = const rax, rbx, rcx, rdx, rsp, rbp, rsi, rdi;
// we include flags here so that we can emulate instructions that access flags.
type Reg32 = const this, mem, eax, ebx, ecx, edx, esi, edi, ebp, esp, f_carry, f_complement_carry, f_direction, f_interrupt;
type Reg16 = const ax, bx, cx, dx, si, di, bp, sp;
type Reg8 = const ah, al, bh,bl, ch,cl, dh,dl;
type RegFloat = const st0, st1, st2, st3, st4, st5, st6, st7;
type RegDouble = const std0;

isRegInt(A) = isReg32(A) or isReg16(A) or isReg8(A);
isReg32(A) = isint (A - eax);
isReg16(A) = isint (A - ax);
isReg8(A) = isint (A - ah);
isRegFloat(A) = isint (A - st0);


// memory datatypes.
type Mem = const memInt, memShort, memByte, memLong, memDouble, memFloat;

// primitive arithmetic operations used for pointers or
// arithmetic operations on bytecode
xaddAux A B = addr([A]) ++ addr([B]) ++ t_add(A);
xsubAux  A B = addr([A]) ++ addr([B]) ++ t_sub(A);
xmulAux A B = addr([A]) ++ addr([B]) ++ t_mul(A);
xdivAux A B = addr([A]) ++ addr([B]) ++ t_div(A);

// to avoid clashes we prepend "x" to all the instructions.
xmov A B = addr(A) ++ value(B) ++  store(A);
// A = A + B
xadd A B = value(A) ++ value(B) ++ store(A); 
// A = A - B
xsub A B = value(A) ++ value(B)  ++ store(A); // to handle different types.
// add with carry
xadc A B = xadd A B;
// sub with borrow
xsbb A B = xsub A B;
// division (unsigned)
xdiv B = [xload(selectRegister(B))] ++ value(B) ++ t_div(B) ++ store(selectRegister(B));
// signed integer divide
xidiv B = xdiv(B);
// multiplication unsigned
xmul B = [xload(selectRegister(B))] ++ value(B) ++ t_mul(B) ++ store(selectRegister(B));
// multiplication signed
ximul B = xmul B;
// increment
xinc A = xadd A 1;
// decrement
xdec A = xadd A (-1) ;
// comparison
xcmp A B = value(A) ++ value(B) ++ t_cmp(A);
// shift arithmetic left
xsal A B = value(A) ++ value(B) ++ t_shl(A);
// shift arithmetic right
xsar A B = value(A) ++ value(B) ++ t_shr(A);
// rotate left through carry
xrcl A B = xsal A B;
// rotate right through carry
xrcr A B = xsar A B;
// rotate left
xrol A B = xsal A B;
// rotate right
xror A B = xsar A B;


// leaving swap to give emphasis of the instruction change
xxchg A B = value(A) ++ value(B) ++ [swap] ++  c_store(B) ++ c_store(A);

// set carry
xstc = value(1) ++ c_store(f_carry);
// clear carry
xclc = value(0) ++ c_store(f_carry);
// set direction
xstd = value(1) ++ c_store(f_direction);
// clear direction
xcld = value(0) ++ c_store(f_direction);
// set interrupt
xsti = value(1) ++ c_store(f_interrupt);
// clear interrupt
xsti = value(0) ++ c_store(f_interrupt);
// push the value into the stack.
xpush A = addr(A) ++  [xload(A)];
// pop the value from the stack into A
xpop A = c_store(A);


// push all flags
xpushf = xpush f_carry ++ xpush f_direction ++ xpush f_interrupt;
// pusha, push all registers
xpusha = xpush eax ++  xpush ebx ++ xpush ecx ++ xpush edx ++ xpush esi ++ xpush edi ++ xpush ebp ++ xpush esp ++ xpush f_carry ++ xpush f_complement_carry ++ xpush f_direction ++ xpush f_interrupt;
// pop all registers
xpopa = xpop f_interrupt ++ xpop f_direction ++ xpop f_complement_carry ++  xpop f_carry ++  xpop esp ++  xpop ebp ++  xpop edi ++  xpop esi ++  xpop edx ++  xpop ecx ++  xpop ebx ++  xpop eax;
// pop all flags
xpopf = xpop f_interrupt ++ xpop f_direction ++ xpop f_carry;



//  *********************************************************************
// Low lever instructions

// obtain an address
// Leave the arrayref and index ready for a reading or writing operation
// addr([A plus C:Int]) = [aload(e( memInt )) , xload(A),  iconst C, iadd] 
// 												 if isRegInt(A) ;

// addr([A minus C:Int]) = [aload(e( memInt )) , xload(A),  iconst C, isub]
// 														if isRegInt(A);
addr(A:Int) = value(A);
addr([A:Int]) = value(A);
addr([A]) = [aload(e( memInt )) , xload(A) ]
							               if isRegInt(A);
addr([A]) =  A  if  islist A;
addr(A) =  A  if  islist A;

// any address is empty
addr(_) = [];


// process adress transformations
A suma B = xaddAux A B;

A resta B = xsubAux A B;

A multi B = xmulAux A B;

A divi B = xdivAux A B;

// obtain a value.
value(A:Int) = [iconst A];
value(A) = addr(A) ++ [ xload(A) ] if isRegInt(A);
value([A]) = addr([A]) ++ [iaload] if isRegInt(A);

// This is for addresses, so we don't need iaload
value([A]) = A ++ [iaload] if islist(A);
value(A) = A ++ [iaload] if islist(A);


// load a value.
xload(A) = iload(e(A)) if isRegInt(A);
xload(A:RegFloat)= fload(e(A));
xload(A:RegDouble) = dload(e(A));

// complete store (includes the address)
c_store(A) = addr(A) ++ store(A);

// Store a value
//8, 16, 32 regs
store(A)=[istore(e(A))] if isRegInt(A);
store([A])= [iastore] if isRegInt(A);
// complex register (since it was already processed, only store the object)
store([A])= [iastore] if islist(A);

// float regs
store(A:RegFloat)=[fstore(e(A))];
store([A:RegFloat])=[fastore];
// double regs
store(A:RegDouble)=[dstore(e(A))];
store([A:RegDouble])=[dastore];


// Addition
t_add(A:RegFloat) = [fadd];
t_add(A:RegDouble) = [dadd];
t_add(A) = [iadd]; // if isRegInt(A);
// Division
t_div(A:RegFloat) = [fdiv];
t_div(A:RegDouble) = [ddiv];
t_div(A) = [idiv]; // if isRegInt(A);
// Substraction
t_sub(A:RegFloat) = [fsub];
t_sub(A:RegDouble) = [dsub];
t_sub(_) = [isub];
// Multiplication
t_mul(A:RegFloat) = [fmul];
t_mul(A:RegDouble) = [dmul];
t_mul(A) = [imul]; // if isRegInt(A);


t_cmp(A:RegFloat) = [fcmp];
t_cmp(A:RegDouble) = [dcmp];
t_cmp(A) = [icmp]; // if isRegInt(A);

// shift left
t_shl(A) = [ishl] if isRegInt(A);

// shift right
t_shr(A) = [ishl] if isRegInt(A);



// used to select the default register
// for some operations for a given type of register.
selectRegister(B:Reg32) = eax;
selectRegister(B:Reg16) = ax;
selectRegister(B:Reg8) = ah;

//e(A:Reg32) = ord(A);
//e(A:Reg16) = ord(A) + #enum_from eax;
//e(A:Mem) = ord(A) + #enum_from eax + #enum_from ax;




