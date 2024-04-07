package music;

import java.util.*;


public class PlaylistLibrary {

    private ArrayList<Playlist> songLibrary; // contains various playlists

   
    public PlaylistLibrary(ArrayList<Playlist> songLibrary) {
        this.songLibrary = songLibrary;
    }

    
    public PlaylistLibrary() {
        this(null);
    }


    public Playlist createPlaylist(String filename) {

        Playlist newPlaylist = new Playlist();
        StdIn.setFile(filename);
        SongNode last = null;

        while (StdIn.hasNextLine())
        {
            String line = StdIn.readLine();
            String[] songInfo = line.split(",");
            Song newSong = new Song(songInfo[0], songInfo[1], Integer.parseInt(songInfo[2]), Integer.parseInt(songInfo[3]), songInfo[4]);
            
            SongNode newNode = new SongNode();
            newNode.setSong(newSong);
            if (newPlaylist.getSize() == 0) {
                newNode.setNext(newNode); 
            } else {
                newNode.setNext(last.getNext());
                last.setNext(newNode);
            }
            last = newNode;

            newPlaylist.setSize(newPlaylist.getSize()+1);
        }
        newPlaylist.setLast(last);

        return newPlaylist; 
    }


    public void addPlaylist(String filename, int playlistIndex) {
        


        if ( songLibrary == null ) {
            songLibrary = new ArrayList<Playlist>();
        }
        if ( playlistIndex >= songLibrary.size() ) {
            songLibrary.add(createPlaylist(filename));
        } else {
            songLibrary.add(playlistIndex, createPlaylist(filename));
        }        
    }


    public boolean removePlaylist(int playlistIndex) {
      

        if ( songLibrary == null || playlistIndex >= songLibrary.size() ) {
            return false;
        }

        songLibrary.remove(playlistIndex);
            
        return true;
    }
    

    public void addAllPlaylists(String[] filenames) {
        
        
        if ( songLibrary == null ) {
            songLibrary = new ArrayList<Playlist>();
        }

        for (String filename : filenames) {
            songLibrary.add(createPlaylist(filename));
        }
    }

    
    public boolean insertSong(int playlistIndex, int position, Song song) {
        
        if (playlistIndex >= songLibrary.size() || position < 1) return false;
        Playlist currPlaylist = songLibrary.get(playlistIndex);
        if (position > currPlaylist.getSize()+1) return false;

        SongNode newNode = new SongNode(song, null);

        if (currPlaylist.getSize() == 0) {
            newNode.setNext(newNode);
            currPlaylist.setLast(newNode);
            currPlaylist.setSize(1);
            return true;
        }

        SongNode prevNode = currPlaylist.getLast();
        SongNode headNode = prevNode.getNext();
        SongNode currNode = headNode;

        int songIndex = position-2;

        while(songIndex > 0) {
            songIndex--;
            prevNode = currNode;
            currNode = currNode.getNext();
        }

        if (position == 1) {
            newNode.setNext(headNode);
            prevNode.setNext(newNode);
        } else {
            newNode.setNext(currNode.getNext());
            currNode.setNext(newNode);
        }

        if(currPlaylist.getSize()+1 == position) currPlaylist.setLast(newNode);
        currPlaylist.setSize(currPlaylist.getSize()+1);

        return true; 
    }

   
    public boolean removeSong(int playlistIndex, Song song) {
        
        if (playlistIndex >= songLibrary.size()) return false;
        Playlist currPlaylist = songLibrary.get(playlistIndex);
        if (currPlaylist.getSize() == 0) return false;

        SongNode headNode = currPlaylist.getLast().getNext();
        SongNode currNode = headNode;
        SongNode prevNode = new SongNode();

        while (!song.equals(currNode.getSong())) {
            if (currNode.getNext() == headNode) return false;

            prevNode = currNode;
            currNode = currNode.getNext();
        }

        if (currNode == headNode && currNode.getNext() == headNode) {
            currNode = null;
            currPlaylist.setLast(null);
        } else if (currNode == headNode) {
            prevNode = currPlaylist.getLast();
            headNode = currNode.getNext();
            prevNode.setNext(headNode);
            currNode = null;

        } else if (currNode.getNext() == headNode) {
            prevNode.setNext(currNode.getNext());
            currNode = null;
            currPlaylist.setLast(prevNode);
        } else {
            prevNode.setNext(currNode.getNext());
        }

        currPlaylist.setSize(currPlaylist.getSize()-1);
        return true;
    }

    
    public void reversePlaylist(int playlistIndex) {
        
        Playlist currPlaylist = songLibrary.get(playlistIndex);
        if (currPlaylist.getSize() <= 1) return;

        SongNode lastNode = currPlaylist.getLast();
        SongNode newlastNode = currPlaylist.getLast().getNext();


        SongNode prevNode = null;
        SongNode currNode = currPlaylist.getLast();

        SongNode nextNode = currNode.getNext();
        currNode.setNext(prevNode);
        prevNode = currNode;
        currNode = nextNode;
        while (currNode != lastNode) {
            nextNode = currNode.getNext();
            currNode.setNext(prevNode);
            prevNode = currNode;
            currNode = nextNode;
        }

        lastNode.setNext(prevNode);
        currPlaylist.setLast(newlastNode);
    }

    private SongNode sortedMerge(SongNode leftNode, SongNode rightNode) {
        if (leftNode == null) return rightNode;
        if (rightNode == null) return leftNode;

        SongNode resultList = null;
        if (leftNode.getSong().getPopularity() >= rightNode.getSong().getPopularity()) {
            resultList = leftNode;
            resultList.setNext(sortedMerge(leftNode.getNext(), rightNode));
        } else {
            resultList = rightNode;
            resultList.setNext(sortedMerge(leftNode, rightNode.getNext()));
        }
        return resultList;
    }

    private SongNode getLast(SongNode headNode) {
        if (headNode == null || headNode.getNext() == null) return headNode;

        SongNode currNode = headNode;

        do {
            currNode = currNode.getNext();
        } while (currNode.getNext() != null);

        return currNode;
    }


    public void mergePlaylists(int playlistIndex1, int playlistIndex2) {
      
        
        if (playlistIndex1 > playlistIndex2) {
            mergePlaylists(playlistIndex2, playlistIndex1);
            return;
        }

        Playlist playlist1 = songLibrary.get(playlistIndex1);
        Playlist playlist2 = songLibrary.get(playlistIndex2);

        if (playlist1.getSize() == 0) {
            playlist1.setSize(playlist2.getSize());
            playlist1.setLast(playlist2.getLast());
            return;
        }
        if (playlist2.getSize() == 0) {
            playlist2.setSize(playlist1.getSize());
            playlist2.setLast(playlist1.getLast());
            return;
        }
        
        SongNode headNode1 = playlist1.getLast().getNext();
        SongNode headNode2 = playlist2.getLast().getNext();
        playlist1.getLast().setNext(null);
        playlist2.getLast().setNext(null);

        SongNode newHeadNode = sortedMerge(headNode1, headNode2);
        SongNode newLastNode = getLast(newHeadNode);
        newLastNode.setNext(newHeadNode);

        playlist1.setLast(newLastNode);
        playlist1.setSize(playlist1.getSize()+playlist2.getSize());
        removePlaylist(playlistIndex2);
    }

    
    public void shufflePlaylist(int playlistIndex) {
        
        if (playlistIndex >= songLibrary.size()) return;

        int newPlaylistIndex = songLibrary.size();
        songLibrary.add(newPlaylistIndex, new Playlist());

        SongNode currNode;
        Song songToMove;

        int position = 1;
        while (songLibrary.get(playlistIndex).getSize() != 0) {
            currNode = songLibrary.get(playlistIndex).getLast().getNext();
            int randNum = StdRandom.uniformInt(songLibrary.get(playlistIndex).getSize()+1);
            // System.out.println("\n");
            // System.out.print(position);
            // System.out.print(":  ");
            // System.out.println(randNum);

            while(randNum > 0) {
                randNum--;
                currNode = currNode.getNext();
            }
            songToMove = currNode.getSong();
            
            removeSong(playlistIndex, songToMove);
            insertSong(newPlaylistIndex, position, songToMove);
            position++;
            
            // System.out.println(songToMove.toString());
            // printPlaylist(playlistIndex);
            // printPlaylist(newPlaylistIndex);
            if(position == 15) break;
        }
        Playlist newPlaylist = songLibrary.get(newPlaylistIndex);
        songLibrary.get(playlistIndex).setLast(newPlaylist.getLast());
        songLibrary.get(playlistIndex).setSize(newPlaylist.getSize());
        songLibrary.remove(newPlaylistIndex);
    }

    private SongNode getMiddle(SongNode headNode) {
        if (headNode == null) return headNode;

        SongNode fastPointer = headNode.getNext();
        SongNode slowPointer = headNode;

        while (fastPointer != null) {
            fastPointer = fastPointer.getNext();
            if (fastPointer != null) {
                slowPointer = slowPointer.getNext();
                fastPointer = fastPointer.getNext();
            }
        }
        return slowPointer;
    }

    private SongNode mergeSort(SongNode headNode) {
        if (headNode == null || headNode.getNext() == null) return headNode;

        SongNode middleNode = getMiddle(headNode);
        SongNode nextOfMiddleNode = middleNode.getNext();
        middleNode.setNext(null);

        SongNode leftNode = mergeSort(headNode);
        SongNode rightNode = mergeSort(nextOfMiddleNode);

        SongNode sortedList =  sortedMerge(leftNode, rightNode);
        return sortedList;
    }


    public void sortPlaylist ( int playlistIndex ) {

        
        if(playlistIndex >= songLibrary.size()) return;

        Playlist currPlaylist = songLibrary.get(playlistIndex);
        SongNode headNode = currPlaylist.getLast().getNext();
        currPlaylist.getLast().setNext(null);

        SongNode newHeaNode = mergeSort(headNode);

        SongNode lastNode = getLast(headNode);
        lastNode.setNext(newHeaNode);
        currPlaylist.setLast(lastNode);
    }

    
    public void playPlaylist(int playlistIndex, int repeats) {
        

        final String NO_SONG_MSG = " has no link to a song! Playing next...";
        if (songLibrary.get(playlistIndex).getLast() == null) {
            StdOut.println("Nothing to play.");
            return;
        }

        SongNode ptr = songLibrary.get(playlistIndex).getLast().getNext(), first = ptr;

        do {
            StdOut.print("\r" + ptr.getSong().toString());
            if (ptr.getSong().getLink() != null) {
                StdAudio.play(ptr.getSong().getLink());
                for (int ii = 0; ii < ptr.getSong().toString().length(); ii++)
                    StdOut.print("\b \b");
            }
            else {
                StdOut.print(NO_SONG_MSG);
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
                for (int ii = 0; ii < NO_SONG_MSG.length(); ii++)
                    StdOut.print("\b \b");
            }

            ptr = ptr.getNext();
            if (ptr == first) repeats--;
        } while (ptr != first || repeats > 0);
    }

    
    public void printPlaylist(int playlistIndex) {
        StdOut.printf("%nPlaylist at index %d (%d song(s)):%n", playlistIndex, songLibrary.get(playlistIndex).getSize());
        if (songLibrary.get(playlistIndex).getLast() == null) {
            StdOut.println("EMPTY");
            return;
        }
        SongNode ptr;
        for (ptr = songLibrary.get(playlistIndex).getLast().getNext(); ptr != songLibrary.get(playlistIndex).getLast(); ptr = ptr.getNext() ) {
            StdOut.print(ptr.getSong().toString() + " -> ");
        }
        if (ptr == songLibrary.get(playlistIndex).getLast()) {
            StdOut.print(songLibrary.get(playlistIndex).getLast().getSong().toString() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    public void printLibrary() {
        if (songLibrary.size() == 0) {
            StdOut.println("\nYour library is empty!");
        } else {
                for (int ii = 0; ii < songLibrary.size(); ii++) {
                printPlaylist(ii);
            }
        }
    }

    
     public ArrayList<Playlist> getPlaylists() { return songLibrary; }
     public void setPlaylists(ArrayList<Playlist> p) { songLibrary = p; }
}
