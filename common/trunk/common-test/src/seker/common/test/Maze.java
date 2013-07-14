/**
 * 
 */
package seker.common.test;

import java.util.Random;

import seker.common.Queue;
import seker.common.Stack;


/**
 * @author seker
 *
 */

public class Maze {

    /**
     * @author liuxinjian
     * @since 2012-8-29
     */
    public static class Cell {
        int i;
        int j;
        int pi;
        int pj;
        
        public Cell(int i, int j, int pi, int pj) {
            this.i = i;
            this.j = j;
            this.pi = pi;
            this.pj = pj;
        }
        
        public int getI() {
            return i;
        }
        
        public int getJ() {
            return j;
        }
        
        public int getPreI() {
            return pi;
        }
        
        public int getPreJ() {
            return pj;
        }
        
        @Override
        public String toString() {
            return String.format("(i=%2d,j=%2d,pi=%2d,pj=%2d)", i, j, pi, pj);
        }
    }

    public static final int PASSABLE    = 0;    // 可通过的
    public static final int IMPASSABLE  = 1;    // 不可通过的
    public static final int OUT         = 8;    // 出口为8
    public static final int STEPPED     = 2;    // 空格为0， 墙为1，已经走过的格子标记为2
    
    int[][] delta = {{0, -1},  {1,0}, {0, 1}, {-1, 0}};/*方向偏移量*/
    
    public static final int ROW         = 5;   /*行列可自定*/
    public static final int COLUM       = 5;   /*行列可自定*/
    
    private Random mRandom = new Random(System.currentTimeMillis());
    
    int[][] maze = //new int[ROW][COLUM];
    //*
     {
     {PASSABLE  , IMPASSABLE, PASSABLE  , IMPASSABLE, IMPASSABLE},
     {PASSABLE  , IMPASSABLE, IMPASSABLE, IMPASSABLE, PASSABLE  },
     {PASSABLE  , PASSABLE  , PASSABLE  , PASSABLE  , IMPASSABLE},
     {PASSABLE  , IMPASSABLE, IMPASSABLE, PASSABLE  , PASSABLE  },
     {IMPASSABLE, IMPASSABLE, PASSABLE  , IMPASSABLE, OUT       }
     };
     //*/

    void createRandomMaze() { /*随机生成迷宫（可能产生走不通的迷宫）*/
        for(int i = 0; i < ROW; i ++) {
            for(int j = 0; j < COLUM; j ++) {
                /*产生 PASSABLE/IMPASSABLE的随机数*/
                maze[i][j] = (0 == mRandom.nextInt() % 2) ? PASSABLE : IMPASSABLE;
            }
        }
    }
    
    void print() { /*打印迷宫*/
        System.out.println("------------------------------");
        for(int i = 0; i < ROW; i ++) {
            for(int j = 0; j < COLUM; j ++) {
                System.out.print(String.format("%2d,", maze[i][j]));
            }
            System.out.println();
        }
    }
    
    /*广度优先遍历算法寻找最短路径*/
    public boolean test(boolean random) {
        boolean flag = false;   /*找到出口标志*/
        int cx,cy;              /*当前位置坐标*/
        int nx,ny;              /*根据方向下一点坐标*/
        
        Queue<Cell> queue = new Queue<Cell>();  /*初始化队列queue*/
        Stack<Cell> stack = new Stack<Cell>();  /*初始化栈stack*/
        
        if (random) {
            createRandomMaze();                 /*创建随机地图*/
        }
        maze[0][0] = PASSABLE;                  /*设置入口*/
        maze[ROW - 1][COLUM - 1] = OUT;         /*设置出口*/
        print();                                /*打印初始时地图*/
        
        Cell temp;
        queue.add(new Cell(0, 0, -1, -1));      /*起始点入队*/
        maze[0][0] = STEPPED;                   /*标记为已访问过了*/
        while((!flag) && (!queue.empty())) {
            temp = queue.delete();              /*出队*/
            stack.push(temp);                   /*所有出队的元素都将别压入栈stack*/
            
            cx = temp.i;
            cy = temp.j;
            
            for(int direction = 0; (!flag) && direction < delta.length; direction ++) {/*朝各个方向试探*/
                nx = cx + delta[direction][0];  /*nx = cx + 方向偏移量*/
                ny = cy + delta[direction][1];  /*ny = cy + 方向偏移量*/

                if(nx < 0 || nx >= ROW || ny < 0 || ny >= COLUM){
                    continue;
                }
                
                switch (maze[ny][nx]) {
                case PASSABLE: {
                    Cell cell = new Cell(nx, ny, cx, cy);   /*下一点的路径上的前驱是当前点*/
                    queue.add(cell);                        /*将下一点入队*/
                    maze[ny][nx] = STEPPED;                 /*标记为已访问过了*/
                    break;
                }
                case OUT: {
                    Cell cell = new Cell(nx, ny, cx, cy);   /*下一点的路径上的前驱是当前点*/
                    queue.add(cell);                        /*将下一点入队*/
                    flag = true;                            /*标志位置TRUE：路径已找到，不用在试探了，退出所有的循环*/
                    break;
                }
                default:
                    break;
                }
            }
        }

        print();    /*打印结束时地图*/
        if(flag) {  /*如果标记为是TRUE，那么说明已经找到路径了，广度优先所的路径即为最短路径*/
            /* 将队列中最后的一点数据到入栈：如果flag为true， 队列中是有数据的，可能有多个，但有用的只会是最后的出口点*/
            while(!queue.empty()) {
                temp = queue.delete();
                stack.push(temp);
            }
            stack.print();

            /* 
             * 以下用到了两个栈:
             * 第一个栈stack用来搜索最短路径并把其存入第二个栈
             * 第二个栈stack_out用于顺序输出
             */
            Stack<Cell> stack_out = new Stack<Cell>();
            Cell start = stack.pop();
            stack_out.push(start);

            while(!stack.empty()) {
                temp = stack.pop();
                if(temp.i == start.pi && temp.j == start.pj) {
                    start = temp;
                    stack_out.push(temp);
                }
            }
            stack_out.print();
        } else {
            System.out.println("failed!\n");
        }
        
        return flag;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        int[][] dxy = {{0,-1}, {1,-1}, {1,0}, {0, 1}, {0, 1}, {-1, 1}, {-1,0}, {-1,-1}};
        System.out.println(String.format("%d, %d", dxy.length, dxy[0].length));
        
        Maze maze = new Maze();
        maze.test(false);
        while(!maze.test(true)){}
    }
}
