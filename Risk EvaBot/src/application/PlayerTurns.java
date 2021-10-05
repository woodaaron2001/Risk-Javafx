package application;
import java.util.Iterator;
//
public class PlayerTurns<Player> implements List<Player>, Iterable<Player>{

    private static class Node<Player>{
        Player element;
        Node<Player> next;

        public Node(Player element){
            this.element = element;
            next = null;
        }
    }

    private Node<Player> cursor;
    private int playerSize = 0;

    public PlayerTurns(){
        cursor = null;
    }

    @Override
    public int size() {
        return playerSize;
    }

    public boolean isEmpty() { return playerSize == 0; }

    @Override
    public Player get(int i) throws IndexOutOfBoundsException {
        Node<Player> temp = cursor;
        for(int k = 0; k < i; ++k){
            temp = temp.next;
        }
        return temp.element;
    }

    public Node<Player> reverse(Node<Player> head){
        if(head == null)
            return null;

        Node<Player> prev = null;
        Node<Player> current = head;
        Node<Player> next;

        do{
            next = current.next; //Next node
            current.next = prev; //We make the current node point to the previous node
            prev = current;     //We then make the previous node = the current
            current = next;     //And then we point to the next node
        }while(current != head);
        //Because it is like a circularly linked list we gotta then reverse the head
        head.next = prev;
        head = prev;
        return head;
    }

    @Override
    public Player set(int i, Player e) throws IndexOutOfBoundsException {
        Node<Player> temp = cursor;
        for(int k = 0; k < i; ++k){
            temp = temp.next;
        }
        Player old = temp.element;
        temp.element = e;
        return old;
    }

    public void add(int i, Player e) throws IndexOutOfBoundsException {
        if(isEmpty()){
            new Node<Player>(e).next = new Node<Player>(e);
        }
        else{
            Node<Player> additional = new Node<Player>(e);
            if(i == 0){
                Node<Player> curr = cursor;
                while (curr.next != cursor)
                    curr = curr.next;

                curr.next = additional;
                additional.next = cursor;
            }else{
                Node<Player> prev = cursor;
                for(int k = 0; k < i - 1; ++k)
                    prev = prev.next;
                Node<Player> after = prev.next;
                prev.next = additional;
                additional.next = after;
            }
        }
        playerSize++;
    }

    @Override
    public Player remove(int i) throws IndexOutOfBoundsException {
        Player deleted;
        if(i == 0){
            deleted = cursor.element;
            Node<Player> prev = cursor;
            while (prev.next != cursor){
                prev = prev.next;
            }
            /*Now the next element will be the tail and since we are removing the tail
             * What we need to do is connect the next element of prev to tail's next
             * And afterwards we must reassign tail to tail's next.*/
            prev.next = cursor.next;
            cursor = cursor.next;
            playerSize--;
            return deleted;
        }

        Node<Player> prev = cursor;
        for(int k = 0; k < i - 1; ++k){
            prev = prev.next;
        }

        Node<Player> del = prev.next;
        deleted = del.element;
        prev.next = del.next;
        playerSize--;
        return deleted;
    }

    @Override
    public Iterator<Player> iterator() {
        return new PlayerTurnsIterator<Player>(this);
    }

    private class PlayerTurnsIterator<Player> implements Iterator<Player>{
        Node<Player> curr;
        PlayerTurns<Player> playerTurns;

        PlayerTurnsIterator(PlayerTurns<Player> input){
            playerTurns = input;
            curr = input.cursor;
        }

        @Override
        public boolean hasNext() {
            return curr.next != playerTurns.cursor;
        }

        @Override
        public Player next() {
            Player ret = curr.element;
            curr = curr.next;
            return ret;
        }
    }

    public Player first(){
        return cursor.element;
    }

    public Player last() {              // returns (but does not remove) the last element
        // TODO
        Node<Player> curr = cursor;
        while (curr.next != curr)
            curr = curr.next;
        return curr.element;
    }

    public Player getCurrentPlayer(){
        return cursor.element;
    }

    // update methods
    /**
     * Rotate the first element to the back of the list.
     */
    public void rotate() {         // rotate the first element to the back of the list
        // TODO
        if(isEmpty())
            return;
        cursor = cursor.next;
    }

    /**
     * Adds an element to the front of the list.
     * @param e  the new element to add
     */
    public void addFirst(Player e) {                // adds element e to the front of the list
        // TODO
        Node<Player> node = new Node<>(e);
        if(isEmpty()){
            cursor = node;
        }
        else{
            Node<Player> last = getLastNode();
            last.next = node;
            node.next = cursor;
            cursor = node;
        }
        playerSize++;
    }

    /**
     * Adds an element to the end of the list.
     * @param e  the new element to add
     */
    public void addLast(Player e) {                 // adds element e to the end of the list
        // TODO
        Node<Player> node = new Node<>(e);
        if(isEmpty()){
            cursor = node;
            cursor.next = cursor;
        }
        else{
            Node<Player> last = getLastNode();
            last.next = node;
            node.next = cursor;
        }
        playerSize++;
    }

    /**
     * Removes and returns the first element of the list.
     * @return the removed element (or null if empty)
     */
    public Player removeFirst() { // removes and returns the first element
        Player del = cursor.element;
        if (playerSize != 1) {
            Node<Player> prev = cursor;
            while (prev.next != cursor)
                prev = prev.next;
            prev.next = cursor.next;
        }
        cursor = cursor.next;
        playerSize--;
        return del;
    }

    public Node<Player> getLastNode(){
        if(isEmpty())
            return null;
        if (playerSize == 1)
            return cursor;
        Node<Player> curr = cursor;
        while(curr.next != cursor)
            curr = curr.next;
        return curr;
    }

    /**
     * Produces a string representation of the contents of the list.
     * This exists for debugging purposes only.
     */
    public String toString() {
        // TODO
        if(isEmpty())
            return null;
        StringBuilder sb = new StringBuilder();
        Node<Player> temp = cursor;
        while(temp.next != cursor){
            sb.append(temp.element).append(" ");
            temp = temp.next;
        }
        sb.append(temp.element);
        return sb.toString();
    }
}
