package models;

import parser.Token;

/**
 * Created by rx on 2017/8/26.
 */
public class Relationship {
    private Column left;
    private Token right;
    private String operator;

    public Relationship() {

    }

    public Relationship(Column left, Token right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Column getLeft() {
        return left;
    }

    public void setLeft(Column left) {
        this.left = left;
    }

    public Token getRight() {
        return right;
    }

    public void setRight(Token right) {
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (object.getClass() != object.getClass()) {
            return false;
        }

        Relationship other = (Relationship) object;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (operator == null) {
            if (other.operator == null) {
                return false;
            }
        } else if (!operator.equals(other.operator)) {
            return false;
        }
        if (right == null) {
            if (other.right != null) {
                return false;
            }
        } else if (!right.equals(other.right)) {
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int res = 1;
        res = prime * res + ((left == null) ? 0 : left.hashCode());
        res = prime * res + ((right == null) ? 0 : right.hashCode());
        res = prime * res + ((right == null) ? 0 : operator.hashCode());

        return res;
    }
    @Override
    public String toString() {
        return left + " " + operator + right;
    }


}
