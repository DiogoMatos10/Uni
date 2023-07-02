.globl main
##################################################################################
#	Licenciatura em Engenharia Informática	       
#	      Arquitetuta de Computadores I            
#		      2022/2023		       
#		Trabalho Realizado por:              
#		 João Santos nº 51966	       
#		 Diogo Matos nº 54466                
##################################################################################
.data
	imageRGB: .string "/home/diogo/starwars.rgb"
	imageFinalRGB: .string "/home/diogo/result.rgb"

	# 320*180*3 =  172800
	inicialBuffer: .space 172800
	
	input: .space 2 #espaço para armazenar a entrada do usuário
	prompt: .string "Digite o número de personagem que pretende, 1(Yoda)|2(Darth Maul)|3(Boba Fett)\n\n->"

.text
##################################################################################
#Função: read_rgb_image	       			 		 
#Descrição:Lê um ficheiro com uma imagem no formato RGB para um array em memória.
#Argumentos:		       					 			
#	a0 - String com o nome do ficheiro            			 
#Retorna:	       							 
#	a2 - Endereço de um buffer onde a imagem deverá ser escrita                                                    
##################################################################################
read_rgb_image:
	#Abre o ficheiro
	li a7, 1024
	la a0, imageRGB
	li a1, 0
	ecall
	
	#Lê o ficheiro
	li a7, 63
	la a1, inicialBuffer
	li a2, 172800
	ecall
	
	#Fecha o ficheiro
	li a7, 57
	mv a0,s6
	ecall
	ret
##################################################################################
#Função: write_rgb_image	       			 		 
#Descrição: Cria um ficheiro novo com uma imagem no formato RGB.		 
#Argumentos:		       					 			
#	a0 - nome do ficheiro					 
#	a1 - buffer da imagem					 
# 	a2 - comprimento do buffer           			           
#Retorna:	       							 
#	Não retorna nenhum valor				                                                   
##################################################################################
write_rgb_image:
	#Abre o ficheiro
	li a7, 1024
	la a0, imageFinalRGB
	li a1, 1
	ecall
	
	#Escreve o ficheiro
	li a7, 64
	la a1, inicialBuffer
	li a2, 172800
	ecall
	
	#Fecha o ficheiro
	li a7, 57
	mv a0, s6
	ecall
	ret
###################################################################################
#Função: hue	       			 		 	 
#Descrição: Calcula a componente Hue a partir das componentes R,G e B.		 
#Argumentos:		       					 			
#	a0 - componente Red
#	a1 - componente Green
#	a2 - componente Blue        			           
#Retorna:	       							 
#	Retorna valor do Hue de um pixel				                                                   
###################################################################################
hue:
	addi sp, sp, -12		#aloca espaço na pilha (3 words)
	sw s8, 0(sp)		#guarda s8 na pilha
	sw s9, 4(sp)		#guarda s9 na pilha
	sw s10, 8(sp)		#guarda s10 na pilha
	
	li t6, 60
	
	#Verificação dos valores
	bgt a0,a1, L1		#R>G>=B	Laranja
	bge a1, a0, L2		#G>=R>B	Verde-amarelado
	bgt a1, a2, L3		#G>B>=R	Verde Primavera
	bge a2, a1, L4		#B>=G>R	Azure
	bgt a2, a0, L5		#B>R>=G	Violeta
	bge a0, a2, L6		#R>=B>G	Rosa
	
	L1:
		bge a1,a2, Calc1
		j L6
		
		Calc1:
			sub s8, a1, a2	#G-B
			sub s9, a0, a2	#R-B
			mul s8, t6, s8 	#60 * (G-B)
			div s8, s8, s9 	#(60 * (G-B))/(R-B)
	j RET
	L2:
		bgt a0,a2, Calc2
		j L3
		
		Calc2: 
			sub s8, a0, a2	#R-B
			sub s9,a1,a2	#G-B
			mul s8, t6, s8	#60 * (R-B)
			div s8, s8, s9  	#(60 * (R-B))/(G-B)
			slli s10, t6, 1 	#2*60
			sub s8, s10, s8 	#120-60*(R-B/G-B)
	j RET
	L3:
		bge a2, a0, Calc3
		j L2
		
		Calc3:
			sub s8, a2, a0	#B-R
			sub s9, a1, a0	#G-R
			mul s8, t6, s8	#60 * (B-R)
			div s8, s8, s9  	#(60 * (B-R))/(G-R)
			slli s10, t6, 1 	#2*60
			add s8, s10, s8 	#120+60*(B-R/G-R)
	j RET		
	L4:
		bgt a1, a0, Calc4
		j L5
		
		Calc4:
			sub s8, a1, a0	#G-R
			sub s9, a2, a0	#B-R
			mul s8, t6, s8	#60 * (G-R)
			div s8, s8, s9  	#(60 * (G-B))/(B-R))
			slli s10, t6, 2 	#4*60
			sub s8, s10, s8 	#240-60*(G-R/B-R)
	j RET		
	L5:
		bge a0, a1, Calc5
		j L4
		
		Calc5:
			sub s8, a0, a1	#R-G
			sub s9, a2, a1	#B-G
			mul s8, t6, s8	#60 * (R-G)
			div s8, s8, s9  	#(60 * (R-G))/(B-G))
			slli s10, t6, 2 	#4*60
			add s8, s10, s8 	#240+60*(R-G/B-G)
	j RET		
	L6:
		bgt a2,a1, Calc6
		j L1
		
		Calc6:
			sub s8, a2, a1	#B-G
			sub s9, a0, a1	#R-G
			mul s8, t6, s8	#60 * (B-G)
			div s8, s8, s9  	#(60 * (B-G))/(R-G))
			addi t6,t6,300	#60+300
			sub s8, t6, s8	#360-60*(B-G/R-G)
	j RET
RET:	
	mv a0, s8		#copia o valor calculado para a0(retorno)
	
	lw s8, 0(sp)	#recupera resultado s8 original da pilha 
	lw s9, 4(sp)	#recupera resultado s9 original da pilha 
	lw s10, 8(sp)	#recupera resultado s10 original da pilha 
	addi sp, sp, 12	#liberta espaço na pilha
	
	ret
##################################################################################
#Função: Indicator	 		 
#Descrição: dado um personagem (e.g. 1,2,3) e um pixel com componentes R, G, B,
#indica se esse pixel pertence ou não à personagem.
#Argumentos:		       					 			
#	a0 - componente Red
#	a1 - componente Green
#	a2 - componente Blue
#	a3 - personagem pretendida     			           
#Retorna:	       							 
#	Retorna 1 se pertencer à personagem pretendida e 0 se não pertencer					                                                   
##################################################################################
indicator:
	addi sp, sp, -4	#aloca espaço na pilha (1 word)
	sw ra, 0(sp)	#guarda ra na pilha
	
	li t1, 1	#Yoda
	li t2, 2	#Maul
	li t3, 3	#Boba
	jal hue		#hue(a0,a1,a2)
	
	beq a3, t1, Yoda	#if(a3==1)->Yoda
	beq a3, t2, Maul	#if(a3==2)->Darth Maul
	beq a3, t3, Boba	#if(a3==3)->Boba Fett
	
Yoda:
	li t4, 40		
	li t5, 80
	blt a0, t4, RET2	#if(hue(a0,a1,a2)<40)->RET2
	bgt a0, t5, RET2	#if(hue(a0,a1,a2)>80)->RET2
	li a0, 1		#resultado do indicator será 1
	j FINAL		

Maul:
	li t4, 1
	li t5, 15
	blt a0, t4, RET2	#if(hue(a0,a1,a2)<1)->RET2
	bgt a0, t5, RET2	#if(hue(a0,a1,a2)>15)->RET2
	li a0, 1		#resultado do indicator será 1
	j FINAL
	
Boba:
	li t4, 160	
	li t5, 180	
	blt a0, t4, RET2	#if(hue(a0,a1,a2)<160)->RET2
	bgt a0, t5, RET2	#if(hue(a0,a1,a2)>180)->RET2
	li a0, 1		#resultado do indicator será 1
	j FINAL
RET2:
	li a0, 0		#resultado do indicator será 0
FINAL:	
	lw ra, 0(sp)	#retira ra da pilha 
	addi sp, sp, 4	#liberta espaço na pilha
	
	ret
##################################################################################
#Função: Location	 
#Descrição: calcula o “centro de massa” para um certo personagem p (a3)
#Argumentos:	 			
#	a3 - personagem pretendida	
#	a4 - buffer com a imagem RGB original           
#Retorna:	       							 
#	Retorna o valor do centro de massa para certo personagem 					                                                   
##################################################################################
location:
	addi sp, sp, -32	#aloca espaço na pilha (8 words)
	sw ra, 0(sp)	#guarda ra na pilha
	sw s0, 4(sp)	#guarda s0 na pilha
	sw s1, 8(sp)	#guarda s1 na pilha
	sw s2, 12(sp)	#guarda s2 na pilha
	sw s3, 16(sp)	#guarda s3 na pilha
	sw s4, 20(sp)	#guarda s4 na pilha
	sw s5, 24(sp)	#guarda s5 na pilha
	sw s6, 28(sp)	#guarda s6 na pilha
	
	
	li s1, 0	#contador N
	li s3, 0	#y
	li s4, 0	#Somador cx
	li s5, 0	#Somador cy
	li t0, 320	#Situação alterada depois de entregue, feita esta alteração, o output ficou correto
	li s6, 180	
	
	WHILE_Y:
		beq s3, s6, RET3	#if(y==180)->RET3
		li s2, 0		#x=0
		jal WHILE_X	
		blt s3, s6, WHILE_Y	#while(y<180)		
		WHILE_X:	
			lbu a0, 0(a4)	#buffer[i]
			lbu a1, 1(a4)	#buffer[i+1]
			lbu a2, 2(a4)	#buffer[i+2]
			jal indicator
			add s1, s1, a0	#N+=indicator()
			addi a4, a4, 3	#i+=3 para avnçar de três em três bytes
			mul a1,s2,a0	#x*indicator()
			mul a2, s3, a0	#y*indicator()
			add s4, s4, a1	#cx+=x
			add s5, s5, a2	#cy+=y
			addi s2, s2, 1	#x++
			blt s2, t0, WHILE_X	#while(x<960)
			addi s3, s3, 1	#y++
			j WHILE_Y
		
RET3:
	div a0, s4, s1	#Cx=Somador Cx/N
	div a1, s5, s1	#Cy=Somador Cy/N
	
	lw ra, 0(sp)	#retira ra da pilha 
	lw s0, 4(sp)	#recupera resultado s0 original da pilha 
	lw s1, 8(sp)	#recupera resultado s1 original da pilha 
	lw s2, 12(sp)	#recupera resultado s2 original da pilha 
	lw s3, 16(sp)	#recupera resultado s3 original da pilha 
	lw s4, 20(sp)	#recupera resultado s4 original da pilha 
	lw s5, 24(sp)	#recupera resultado s5 original da pilha 
	lw s6, 28(sp)	#recupera resultado s6 original da pilha 
	addi sp, sp, 32	#liberta espaço na pilha
	
	ret	
##################################################################################
#Função: Cross	
#Descrição: Altera o Buffer inicial, coloca uma cruz na personagem pretendida
#Argumentos:	
#	a0 - Cx  	
#  	a1 - Cy		
#	a3 - personagem pretendida
#	a4 - buffer com a imagem RGB original   	           
#Retorna:	       							 
#	Retorna o buffer final com a identificação da personagem pretendida				                                                   
##################################################################################

cross:		
	addi sp, sp, -24	#aloca espaço na pilha (3 words)
	sw s0, 0(sp)		#guarda s0 na pilha
	sw s1, 4(sp)		#guarda s1 na pilha
	sw s2, 8(sp)		#guarda s2 na pilha
	sw s3, 12(sp)		#guarda s3 na pilha
	sw s4, 16(sp)		#guarda s4 na pilha
	sw s5, 20(sp)		#guarda s5 na pilha
		
	li t1, 1	#Yoda
	li t2, 2	#Maul
	li t3, 3	#Boba
		
	li t5, 960	#3*320
	li t6, 172800	#320*180*3 =  172800
	
	mul a1, t5, a1	#cy*960
	li t0, 3	
	mul a0, a0, t0	#cx*3
	sub t4,t5,a0	#960-cx
	sub s3,a1, t4	#(cy*960)-(960-cx)
	
	VERIFY:
		beq a3, t1, Cross_Yoda		# Verifica se a3 é igual a t1 (condição para o personagem Yoda)
		beq a3, t2, Cross_Maul		# Verifica se a3 é igual a t2 (condição para o personagem Maul)
		beq a3, t3, Cross_Boba		# Verifica se a3 é igual a t3 (condição para o personagem Boba)
	
	Cross_Yoda:
		li a0, 255	#R			
		li a1, 0		#G				
		li a2, 0		#B				
		
		
		#Todo o procedimento abaixo foi para desenhar a cruz
		add a4, a4, s3				# Adiciona o valor de s3 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		
	li s4, 0
	li s5, 20	
	loop_direita_Yoda:
		beq s4, s5, continua_Yoda			# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, -3				# Subtrai 3 de a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_direita_Yoda				# Salta para loop_direita_Yoda
		
		continua_Yoda:
		li s4, 0
		li s5, 11
		
	loop_esquerda_Yoda:
		beq s4, s5, continua2_Yoda			# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, 3				# Adiciona 3 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_esquerda_Yoda			# Salta para loop_esquerda_Yoda
		
		continua2_Yoda:
		li s4, 0
		li s5, 10
		
		addi a4, a4, -3				# Subtrai 3 de a4
	loop_cima_Yoda:
		beq s4, s5, continua3_Yoda			# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, -960				# Subtrai 960 de a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_cima_Yoda				# Salta para loop_cima_Yoda
		
		continua3_Yoda:
		li s4, 0
		li s5, 20
	loop_baixo_Yoda:
		beq s4, s5, RET4				# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, 960				# Adiciona 960 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_baixo_Yoda				# Salta para loop_baixo_Yoda
	
	Cross_Maul:
		li a0, 0		#R				
		li a1, 255	#G				
		li a2, 0		#B				
		
		#Todo o procedimento abaixo foi para desenhar a cruz
		li s4, 0
		li s5, 20	
	
		add a4, a4, s3				# Adiciona o valor de s3 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
	loop_direita_Maul:
		beq s4, s5, continua_Maul			# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, -3				# Subtrai 3 de a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_direita_Maul				# Salta para loop_direita_Maul
		
		continua_Maul:
		li s4, 0
		li s5, 11
		
	loop_esquerda_Maul:
		beq s4, s5, continua2_Maul			# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, 3				# Adiciona 3 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_esquerda_Maul			# Salta para loop_esquerda_Maul
		
		continua2_Maul:
		li s4, 0
		li s5, 10

		addi a4, a4, -3				# Subtrai 3 de a4
	loop_cima_Maul:
		beq s4, s5, continua3_Maul			# Verifica se s4 é igual a s5 (condição de término do loop)
		
		addi a4, a4, -960				# Subtrai 960 de a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_cima_Maul				# Salta para loop_cima_Maul
		
		continua3_Maul:
		li s4, 0
		li s5, 20
		
	loop_baixo_Maul:
		beq s4, s5, RET4				# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, 960				# Adiciona 960 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_baixo_Maul				# Salta para loop_baixo_Maul
	
	Cross_Boba:
		li a0, 255	#R				
		li a1, 0		#G				
		li a2, 0		#B				
		
		#Todo o procedimento abaixo foi para desenhar a cruz
		li s4, 0
		li s5, 20	
	
		add a4, a4, s3				# Adiciona o valor de s3 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
	loop_direita_Boba:
		beq s4, s5, continua_Boba			# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, -3				# Subtrai 3 de a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_direita_Boba				# Salta para loop_direita_Boba
		
		continua_Boba:
		li s4, 0
		li s5, 11
		
	loop_esquerda_Boba:
		beq s4, s5, continua2_Boba			# Verifica se s4 é igual a s5 
		addi a4, a4, 3				# Adiciona 3 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_esquerda_Boba			# Salta para loop_esquerda_Boba
		
		continua2_Boba:
		li s4, 0
		li s5, 10
		
		addi a4, a4, -3				# Subtrai 3 de a4
	loop_cima_Boba:
		beq s4, s5, continua3_Boba			# Verifica se s4 é igual a s5 (condição de término do loop)
		
		addi a4, a4, -960				# Subtrai 960 de a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_cima_Boba				# Salta para loop_cima_Boba
		
		continua3_Boba:
		li s4, 0
		li s5, 20
		
	loop_baixo_Boba:
		beq s4, s5, RET4				# Verifica se s4 é igual a s5 (condição de término do loop)
		addi a4, a4, 960				# Adiciona 960 a a4
		sb a0, 0(a4)				# Armazena o valor de a0 (R) em a4
		sb a1, 1(a4)				# Armazena o valor de a1 (G) em a4 + 1
		sb a2, 2(a4)				# Armazena o valor de a2 (B) em a4 + 2
		addi s4, s4, 1				# Incrementa s4 em 1
		j loop_baixo_Boba				# Salta para loop_baixo_Boba

RET4:	
	lw s0, 0(sp)		#recupera resultado s0 original da pilha
	lw s1, 4(sp)		#recupera resultado s1 original da pilha
	lw s2, 8(sp)		#recupera resultado s2 original da pilha
	lw s3, 12(sp)		#recupera resultado s3 original da pilha
	lw s4, 16(sp)		#recupera resultado s4 original da pilha
	lw s5, 20(sp)		#recupera resultado s5 original da pilha
	addi sp, sp, 24		#liberta espaço na pilha
	
	ret	
################################################################################
#Funcao: Main
#Descricao: inicialmente foi implementado o input para receber a personagem pretendida e posteriormente a chamada das funções
#Argumentos:
#  Sem argumentos
#Retorna:
#  Sem Valor de retorno 
################################################################################
main:
	#Input da personagem pretendida
	la a0, prompt
	li a7, 4
	ecall
	li a7, 5
	ecall
	mv a3, a0		
		
	
	addi sp, sp, -4	#aloca espaço na pilha (1 word)
	sw s11, 0(sp)	#guarda s11 na pilha
	
		
	la a0, imageRGB
	jal read_rgb_image
	
	la s11, inicialBuffer
	mv a4, s11
	
	jal location	#location(a3,a4)
	
	mv a4, s11
	jal cross		#cross(a0,a1,a3,a4)
	jal write_rgb_image
			
	lw s11, 0(sp)	#recupera resultado s11 original da pilha
	addi sp, sp, 4	#liberta espaço na pilha