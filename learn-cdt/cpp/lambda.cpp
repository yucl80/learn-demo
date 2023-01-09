#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

void foo() { std::cout << "foo()\n"; }
void bar() { std::cout << "bar()\n"; }

int test() { return 4 ;}

int main ()
{
	// 1st lambda function
	auto f  = []() {
		foo();
		bar();
	};
	// the lambda function does something here
	f();

        // 2nd lambda function
	std::vector<int> v(5, 99);
	std::for_each(v.begin(), v.end(), [](int i){std::cout << i << "\n";});

	cout << [](int n) {return n*n;} (5);
    cout << endl;
      /* case #2 - explicit return type */
    cout << [](int n)->int {return n*n;} (5);

    // (1)
        std::cout << [](int a, int b){return a*b; }(test(), 5) << std::endl; // 20

        // (2)
    auto f = [](int a, int b) { return a*b; };
    std::cout << f(4, 5) << std::endl;  // 20

	return 0;
}