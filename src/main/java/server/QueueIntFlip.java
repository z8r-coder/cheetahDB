package server;

/**
 * Created by rx on 2017/9/8.
 */
public class QueueIntFlip {
    private int[] elements = null;

    private int capacity = 0;
    private int writePos = 0;
    private int readPos = 0;
    public boolean flipped = false;

    public QueueIntFlip(int capacity) {
        this.capacity = capacity;
        this.elements = new int[capacity];
    }

    /**
     * 重置
     */
    public void reset() {
        this.writePos = 0;
        this.readPos = 0;
        this.flipped = false;
    }

    public int available() {
        if (!flipped) {
            return writePos - readPos;
        }
        return capacity - readPos + writePos;
    }

    public int remainingCapacity() {
        if (!flipped) {
            return capacity - writePos;
        }
        return readPos - writePos;
    }

    /**
     * 放入一个元素
     * @param element
     * @return
     */
    public boolean put(int element) {
        if (!flipped) {
            if (writePos == capacity) {
                writePos = 0;
                flipped = true;

                if (writePos < readPos) {
                    elements[writePos++] = element;
                    return true;
                } else {
                    return false;
                }
            } else {
                elements[writePos++] = element;
                return true;
            }
        } else {
            if (writePos < readPos) {
                elements[writePos++] = element;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 放入数组元素
     * @param newElements
     * @param length
     * @return
     */
    public int put(int[] newElements, int length) {
        int newElementsReadPos = 0;
        if (!flipped) {
            if (length <= capacity - writePos) {
                for (;newElementsReadPos < length; newElementsReadPos++) {
                    this.elements[this.writePos++] = newElements[newElementsReadPos];
                }
                return newElementsReadPos;
            } else {
                for (;this.writePos < capacity;this.writePos++) {
                    this.elements[this.writePos] = newElements[newElementsReadPos++];
                }

                this.writePos = 0;
                this.flipped = true;
                int endPos = Math.min(this.readPos, length - newElementsReadPos);
                for (; this.writePos < endPos;this.writePos++) {
                    this.elements[writePos] = newElements[newElementsReadPos++];
                }

                return newElementsReadPos;
            }
        } else {
            int endPos = Math.min(this.readPos, this.writePos + length);

            for (;this.writePos < endPos; this.writePos++) {
                this.elements[this.writePos] = newElements[newElementsReadPos];
            }

            return newElementsReadPos;
        }
    }

    /**
     * 取出一个元素
     * @return
     */
    public int take() {
        if (!flipped) {
            if (readPos < writePos) {
                return elements[readPos];
            } else {
                return -1;
            }
        } else {
            if (readPos == capacity) {
                readPos = 0;
                flipped = false;

                if (readPos < writePos) {
                    return elements[readPos++];
                } else {
                    return -1;
                }
            } else {
                return elements[readPos++];
            }
        }
    }

    /**
     * 取出固定长度数组
     * @param into
     * @param length
     * @return
     */
    public int take(int[] into, int length) {
        int intoWritePos = 0;
        if (!flipped) {
            int endPos = Math.min(this.writePos, this.readPos + length);

            for (;this.readPos < endPos; this.readPos++) {
                into[intoWritePos++] = this.elements[this.readPos];
            }
            return intoWritePos;
        } else {
            if (length <= capacity -readPos) {
                for (; intoWritePos < length; intoWritePos++) {
                    into[intoWritePos] = this.elements[this.readPos++];
                }

                return intoWritePos;
            } else {

                for (; this.readPos < capacity; this.readPos++) {
                    into[intoWritePos++] = this.elements[this.readPos];
                }

                this.readPos = 0;
                this.flipped = false;
                int endPos = Math.min(this.writePos, length - intoWritePos);
                for (; this.readPos < endPos; this.readPos++) {
                    into[intoWritePos++] = this.elements[this.readPos];
                }

                return intoWritePos;
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int getWritePos() {
        return writePos;
    }

    public int getReadPos() {
        return readPos;
    }

    public void setWritePos(int writePos) {
        this.writePos = writePos;
    }

    public void setReadPos(int readPos) {
        this.readPos = readPos;
    }

    public static void main(String arg[]) {
        QueueIntFlip queueIntFlip = new QueueIntFlip(100);
    }
}
