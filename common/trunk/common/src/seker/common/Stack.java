package seker.common;

/**
 * 栈的定义及其运算
 *  1、栈的定义
 *      堆栈（Stack）可以看成是一种“特殊的”线性表，这种线性表上的插入和删除运算限定在表的某一端进行的。
 *          (1)通常称插入、删除的这一端为栈顶（Top），另一端称为栈底（Bottom）。
 *          (2)当表中没有元素时称为空栈。
 *          (3)栈为后进先出（Last In First Out）的线性表，简称为LIFO表。
 *    栈的修改是按后进先出的原则进行。每次删除（退栈）的总是当前栈中"最新"的元素，即最后插入（进栈）的元素，
 *    而最先插入的是被放在栈的底部，要到最后才能删除。
 * 
 * 2、栈的基本运算
 * （1）InitStack（S）
 *    初始化操作，设定一个空栈S。
 * （2）EmptyStack（S）
 *    判空栈操作，若S为空栈，则返回TRUE，否则返回FALSE。
 * （3）FullStack（S）
 *    判栈满。若S为满栈，则返回TRUE，否则返回FALSE。
 * 注意：
 *    该运算只适用于栈的顺序存储结构。
 * （4）Push（S，e）
 *    进栈。若栈S不满，在S栈顶插入一个元素e，栈顶位置由top指针指出。
 * （5）Pop（S）
 *    退栈。若栈S非空，则将S的栈顶元素删去，并返回该元素。
 * （6）GetTop（S）
 *    取栈顶元素。若栈S非空，则返回栈顶元素，但不改变栈的状态。
 * （7）ClearStack（S）
 *    置栈空操作，已知S为栈，不论操作之前的栈是否为空栈，本操作的结果都是将S置为空栈。
 * （8）CurrentStack（S）
 *    求当前栈中元素的个数。
 * （9）DestroyStack（S）
 *    销毁S栈。
 */
public class Stack<E> {
    
    protected java.util.LinkedList<E> list = new java.util.LinkedList<E>();
    
    public boolean empty() {
        return list.isEmpty();
    }
    
    public void push(E e) {
        list.addFirst(e);
    }
    
    public synchronized E pop() {
    	if(!list.isEmpty()){
    		return list.removeFirst();
    	}else{
    		return null;
    	}
    }
    
    public synchronized E peek() {
        return list.getFirst();
    }
    
    public synchronized int search(Object o) {
        int i = list.lastIndexOf(o);
        if (i >= 0) {
            return list.size() - i;
        }
        return -1;
    }
    
    public int size(){
        return list.size();
    }
    
    public void clear() {
        list.clear();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName()).append(": ");
        if (!empty()) {
            for (E e : list) {
                builder.append(e).append(" -> ");
            }
            int length = builder.length();
            builder.delete(length - 4, length - 1);
        }
        return builder.toString();
    }
    
    public void print() {
        System.out.println(toString());
    }
}
