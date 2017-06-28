#ifndef student_h
#define student_h

#include<string>

class Student {
  private:
	std::string name;
  public:
	Student(std::string);
	virtual void display();
};


#endif