import java.io.File;
import java.io.IOException;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;

/*
 * SpeX.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

/**
 * A simple Hello World example for how to use Spex.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class HelloWorld {

	/**
	 * This method creates a simple XML document in your
	 * current directory. Note that even this simple example uses
	 * the full set of features provided by Spex, outlining
	 * its lightweight nature.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// The file we want to write our XML document to
		File file = new File("SpexHelloWorld.xml");
		// Now we create a Spex document, using the file for output.
		SXDocument doc = new SXDocument(file);
		// Add the root node to our new document.
		SXTag root = doc.addNode("HelloWorld");
		// The SXTag handle allows us to add attributes to our
		// newly created node, until we add child nodes.
		root.addAttribute("createdBy", "Spex");
		root.addAttribute("coolness", "extreme");
		// We can add XML comments anywhere in the document.
		root.addComment("Now for some child nodes...");
		// Adding child nodes works in the same way as adding
		// the root node.
		for(int i=0; i<3; i++) {
			SXTag child = root.addChildNode("Howdy");
			child.addAttribute("childNumber", Integer.toString(i));
			// Of course, any node can have child nodes of their own.
			SXTag grandChild = child.addChildNode("Aloha");
			// Adding an XML text node goes like this.
			grandChild.addTextNode("This is child number " + i + ".");
			// Note that we do not have to close any nodes. Nodes are 
			// implicitly closed when we add another child node to
			// their parent.
		}
		// Do not forget to close the document when you are finished!
		// Otherwise, your resulting document may be incomplete.
		doc.close();
	}

}
