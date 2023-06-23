package Project5;


public class Stack {
	private Object[] objects;
	private int topOfStack;
	public Stack(int capacity) {
		objects = new Object[capacity];
		topOfStack = - 1;
	}
	public boolean isEmpty() {
		if(topOfStack == -1) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isFull() {
		return (topOfStack + 1 == objects.length);
	}
	public void push(Object info) {
		if(isFull()) {
			System.out.println("Stack overflow");
		}
		else {
			topOfStack++;
			objects[topOfStack] = info;
		}
	}
	public Object pop() {
		if(isEmpty()) {
			System.out.println("Stack is empty.");
			return null;
		}
		else {
			Object returnData = objects[topOfStack];
			topOfStack--;
			return returnData;
		}
	}
	public Object peek() {
		if(isEmpty()) {
			System.out.println("Stack is empty.");
			return null;
		}
		else {
			return objects[topOfStack];
		}
	}
	public int size() {
		return topOfStack + 1;
	}
}
