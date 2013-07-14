package seker.common;

/**
 * 
 * 队列的定义及其运算
 * 
 * 1、定义
 *     队列（Queue）是只允许在一端进行插入，而在另一端进行删除的运算受限的线性表。
 * （1）允许删除的一端称为队头（Front）。
 * （2）允许插入的一端称为队尾（Rear）。
 * （3）当队列中没有元素时称为空队列。
 * （4）队列亦称作先进先出（First In First Out）的线性表，简称为FIFO表。
 * 
 * 队列的结构特点是先进队的元素先出队。假设有队列Q=(a1,a2,...,an),则队列Q中的元素是按a1,a2,...,an的次序进队,
 * 而第一个出队的应该是a1,第二个出队的应该是a2,只有在ai-1出队后, ai才可以出队(1≤i≤n)。
 * 
 * 队列的修改是依先进先出的原则进行的。新来的成员总是加入队尾（即不允许"加塞"），每次离开的成员总是队列头上的（不允许中途离队），即当前"最老的"成员离队。
 *  
 * 2、队列的基本逻辑运算
 * 与栈类似,队列的运算可以归纳为以下几种:
 * （1）AddQ(ElemType x)
 *      ——在队列的尾部插入一个新的元素x。队尾的位置由rear指出。
 * （2）DelQ(Q)
 *      ——删除队列的队头的元素。队头的位置由front指出。
 * （3）EmptyQ(Q)
 *      ——测试队列Q是否为空队。当队列为空时返回一个真值,否则返回一个假值。
 * （4）FrontQ(Q)
 *      ——取得队列Q的队头元素。该运算与DelQ(Q)不同,后者要修改队头元素指针。
 * （5）SetNULL(Q)
 *      ——创建一个空队Q,这个运算与线性表置空表类似。
 */
public class Queue<E> {
    
    protected java.util.LinkedList<E> list = new java.util.LinkedList<E>();
    
    public boolean empty() {
        return list.isEmpty();
    }
    
    public void add(E e) {
        list.addLast(e);
    }
    
    public synchronized E delete() {
        if(!list.isEmpty()){
            return list.removeFirst();
        } else {
            return null;
        }
    }
    
    public synchronized E font() {
        return list.getFirst();
    }
    
    public synchronized int search(Object o) {
        int i = list.lastIndexOf(o);
        if (i >= 0) {
            return list.size() - i;
        }
        return -1;
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
