read(x);
read(y);
read(z);
case
    when (x>y) case
                when (x>z) print(x)
                else print(z)
    else case 
            when (y>z) print(y)
            else print(z)