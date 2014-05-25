ЛР по Методам Построения Трансляторов (УГАТУ 3 курс 6 семестр)
===============================================================

## Лабораторная работа №3

Вар 3, грам 3, Идентификаторы, десятичные числа с плавающей точкой

G({if,then,else,a,:=,<,>,==,(,),;},{S,F,E,D,C},P,S)

S -> F
<br/> F -> if E then T else F | if E then F | a := E
<br/> T -> if E then T else T | a := E
<br/> E -> E > D | E < D | D
<br/> D -> D = C | C
<br/> C -> a | (E)

### Множества крайних левых и крайних правых символов

Символ U	| L(U)				| R(U)
<br/> ---------------------------------------------------
<br/> S			| F, if, a			| ;
<br/> F			| if, a				| F, E, D, C, a, )
<br/> T			| if, a				| T, E, D, C, a, )
<br/> E			| E, D, C, a, (		| D, C, a, )
<br/> D			| D, C, a, (		| C, a, )
<br/> C			| a, (				| a, )


### Множества крайних левых и крайних правых терминальных символов

Символ U	| L(U)				| R(U)
<br/> ---------------------------------------------------
<br/> S			| if, a, ;			| ;
<br/> F			| if, a				| else, then, :=, <, >, ==, a, )
<br/> T			| if, a				| else, :=, <, > ==, a, )
<br/> E			| <, >, ==, a, (	| <, >, ==, a, )
<br/> D			| ==, a, (			| ==, a, )
<br/> C			| a, (				| a, )

### Остовная грамматика

E -> E; - правило 1;
<br/> E -> if E then E else E | if E then E |  a := E - правила 2, 3, и 4;
<br/> E -> if E then E else E | if a := E - правила 5 и 6;
<br/> E -> E < E | E > E | E - правила 7, 8, 9;
<br/> E -> E == E | E - правила 10 и 11;
<br/> E -> a | (E) - правила 12 и 13;