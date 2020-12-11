package com.neowise.game.homeBase;

public class CLabel {
	
	public int name;
	public CLabel root;
	public int rank;
	
	CLabel(int name){
		this.name = name;
		this.root = this;
		this.rank = 0;
	}
	
	public CLabel getRoot() {
		if (this.root != this)
		{
			this.root = this.root.getRoot();
		}

		return this.root;
	}
	
    public void Join(CLabel root2)
    {
        if (root2.rank < this.rank)//is the rank of Root2 less than that of Root1 ?
        {
            root2.root = this;//yes! then Root1 is the parent of Root2 (since it has the higher rank)
        }
        else //rank of Root2 is greater than or equal to that of Root1
        {
            this.root = root2;//make Root2 the parent

            if (this.rank == root2.rank)//both ranks are equal ?
            {
                root2.rank++;//increment Root2, we need to reach a single root for the whole tree
            }
        }
    }
	
	

}
