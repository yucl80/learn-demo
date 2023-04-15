#include <iostream>
#include <vector>
#include <algorithm>


using namespace std;

void foo() { std::cout << "foo()\n"; }

void bar() { std::cout << "bar()\n"; }

class Test {
public:
    void hello(){
        cout << "hello";
    }
};

class Vehicle {
public:
    string brand = "Ford";
    string name = "oo";

    void honk() {
        cout << "Tuut, tuut! \n";
    }

    Vehicle(){
        cout << "Hello World!";
    }
private:
    int age =18;
};

// Derived class
class Car : public Vehicle {
public:
    string model = "Mustang";
    string brand;  // Attribute

    int year;      // Attribute

    int sum(int k) {

        if (k > 0) {
            return k + sum(k - 1);
        } else {
            return 0;
        }
        foo();
    };
    Car(){
        bar();
        cout << "sssssss";
    }

    Car(string x, string y, int z);
    int fun2(int a);
    int fun2(long a);
    void swapNums(int &x, int &y);
};


Car::Car(string x, string y, int z) {
    brand = x;
    model = y;
    year = z;
    bar();
}

int Car::fun2(int a) {
    Test test;
    test.hello();
    return 0;
}

int Car::fun2(long a) {
    Test test;
    test.hello();
    return 0;
}
void Car::swapNums(int &x, int &y) {
  int z = x;
  x = y;
  y = z;
}

int test() { return 4; }

int fun1(string str){
#line 100 "sdfdf"
    Car myCar;
    myCar.honk();
    cout << myCar.brand + " " + myCar.model;
    // 1st lambda function
    auto f = []() {
        foo();
        bar();
    };
    // the lambda function does something here
    f();

    // 2nd lambda function
    std::vector<int> v(5, 99);
    std::for_each(v.begin(), v.end(), [&myCar](int i) { std::cout << myCar.sum(i) << "\n"; });
    int result = myCar.sum(10);
    cout << result;
    cout << str;
    myCar.fun2(1);
    myCar.fun2(1L);
    int a=100;
    int b=10;
    myCar.swapNums(a,b);
}

int main() {

    cout << [](int n) { return n * n; }(5);
    cout << endl;
    /* case #2 - explicit return type */
    cout << [](int n) -> int { return n * n; }(5);

    // (1)
    std::cout << [](int a, int b) { return a * b; }(test(), 5) << std::endl; // 20

    // (2)
    auto f2 = [](int a, int b) { fun1("ok"); return  a * b; };
    std::cout << f2(4, 5) << std::endl;  // 20

     fun1("this is a test");
    return 0;
}