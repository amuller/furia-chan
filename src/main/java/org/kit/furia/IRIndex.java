package org.kit.furia;

import org.ajmm.obsearch.OB;

import java.io.IOException;
import java.util.List;
import org.ajmm.obsearch.Index;
import org.ajmm.obsearch.exception.AlreadyFrozenException;
import org.ajmm.obsearch.exception.IllegalIdException;
import org.ajmm.obsearch.exception.OBException;
import org.ajmm.obsearch.exception.OutOfRangeException;
import org.ajmm.obsearch.exception.UndefinedPivotsException;
import org.apache.lucene.index.CorruptIndexException;
import org.kit.furia.exceptions.IRException;

import com.sleepycat.je.DatabaseException;

/*
 Furia-chan: An Open Source software license violation detector.
 Copyright (C) 2007 Kyushu Institute of Technology

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * IRIndex holds the basic functionality for an Information Retrieval system
 * that works on OB objects (please see obsearch.berlios.de). By using a
 * distance function d, we transform the queries in terms of the closest
 * elements that are in the database, and once this transformation is performed,
 * we utilize an information retrieval system to perform the matching. Because
 * our documents are multi-sets, the distribution of OB objects inside a
 * document is taken into account. So, instead of matching a huge syntax tree of
 * for example, music, we cut a song into pieces, match the pieces and then the
 * overall finger-print of the multi-set of OB objects is matched.
 * @author Arnoldo Jose Muller Molina
 * @since 0
 */
public interface IRIndex < O extends OB > {

    /**
     * Inserts a new document into the database.
     * @param document
     *                The document to be inserted.
     * @throws IRException
     *                 If something goes wrong with the IR engine or with
     *                 OBSearch.
     */
    void insert(Document < O > document) throws IRException;

    /**
     * Deletes the given string document from the database. If more than one
     * documents have the same name, all the documents will be erased.
     * @return The number of documents deleted.
     * @throws IRException
     *                 If something goes wrong with the IR engine or with
     *                 OBSearch.
     */
    int delete(String documentName) throws IRException;

    /**
     * Returns the underlying OBSearch index.
     * @return the underlying OBSearch index.
     */
    Index < O > getIndex();

    /**
     * Freezes the index. From this point data can be inserted, searched and
     * deleted. The index might deteriorate at some point so every once in a
     * while it is a good idea to rebuild the index. This method will also
     * @throws IRException
     *                 If something goes wrong with the IR engine or with
     *                 OBSearch.
     */
    void freeze() throws IRException;

    /**
     * Closes the databases. You *should* close the databases after using an
     * IRIndex.
     * @throws IRException
     *                 If something goes wrong with the IR engine or with
     *                 OBSearch.
     */
    void close() throws IRException;

    /**
     * Returns the number of documents stored in this index.
     * @return the number of documents stored in this index.
     */
    int getSize();

    /**
     * Returns true if the document corresponding to x's name exists in the DB.
     * This method is intended to be used in validation mode only.
     * @param x
     * @return true if the DB does not contain a document with name x.getName()
     */
    boolean shouldSkipDoc(Document<O> x) throws IOException;
    
    /**
     * The M-set score threshold is the minimum naive score for multi-sets
     * that the index will accept.
     * @return Returns the current M-set score threshold.
     */
    float getMSetScoreThreshold();

    /**
     * The M-set score threshold is the minimum naive score for multi-sets
     * that the index will accept.
     * @param setScoreThreshold the new threshold
     */
    void setMSetScoreThreshold(float setScoreThreshold);

    /**
     * * The Set score threshold is the minimum naive score for Sets
     * that the index will accept.
     * @return Returns the current Set score threshold.
     */
    float getSetScoreThreshold();
    /**
     * The Set score threshold is the minimum naive score for Sets
     * that the index will accept.
     * @param setScoreThreshold the new threshold
     */
    void setSetScoreThreshold(float setScoreThreshold);
    
    /**
     * Returns the count different words that
     * are used by the documents indexed. 
     * @return the count different words that
     * are used by the documents indexed. 
     */
    int getWordsSize() throws DatabaseException;
    
    /**
     * Tells whether or not the index is in validation mode. 
     * In validation mode we assume that documents with the same name are equal.
     * This helps us to add additional statistics on the performance of the scoring technique.
     * @return true if this index is in validation mode.
     */
    boolean isValidationMode();
    /**
     * Sets whether or not the index is in validation mode. 
     * In validation mode we assume that documents with the same name are equal.
     * This helps us to add additional statistics on the performance of the scoring technique.
     * @param validationMode The new validation mode.
     * */
    void setValidationMode(boolean validationMode);
}
