#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

void foo() { std::cout << "foo()\n"; }

void bar() { std::cout << "bar()\n"; }

class Vehicle {
public:
    string brand = "Ford";
    String name = "oo";

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
    };
   Car(string x, string y, int z);
};

Car::Car(string x, string y, int z) {
  brand = x;
  model = y;
  year = z;
}

int test() { return 4; }

int main() {
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
    std::for_each(v.begin(), v.end(), [](int i) { std::cout << sum(i) << "\n"; });

    cout << [](int n) { return n * n; }(5);
    cout << endl;
    /* case #2 - explicit return type */
    cout << [](int n) -> int { return n * n; }(5);

    // (1)
    std::cout << [](int a, int b) { return a * b; }(test(), 5) << std::endl; // 20

    // (2)
    auto f2 = [](int a, int b) { return a * b; };
    std::cout << f2(4, 5) << std::endl;  // 20

    int result = myCar.sum(10);
    cout << result;
    return 0;
}