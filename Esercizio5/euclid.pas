read(a);
read(b);
while (a<>b) 
	case 
		when (a>b) a:=a-b
		else b:=b-a;
print(a)