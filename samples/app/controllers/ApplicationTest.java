package controllers;

import java.util.List;

import javax.inject.Inject;

import models.Account;
import models.Item;
import play.Logger;
import play.exceptions.UnexpectedException;
import play.modules.orientdb.ODB.DBTYPE;
import play.modules.orientdb.Transactional;
import play.mvc.Controller;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObjectTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.iterator.OObjectIteratorMultiCluster;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ApplicationTest extends Controller {
    @Inject
    static ODatabaseObjectTx db;

    @Inject
    static ODatabaseDocumentTx docdb;
    
    @Inject
    static OGraphDatabase graphdb;

    public static void index() {
        List<Item> result = Item.find("select * from Item where name like ?", "my%");
        OObjectIteratorMultiCluster<Item> items = Item.all();
        OObjectIteratorMultiCluster<Account> accounts = db.browseClass(Account.class);
        render(result, items, accounts);
    }

    public static void detail(ORecordId id) {
        Item item = Item.findById(id);
        notFoundIfNull(item);
        render(item);
    }
    
    public static void testGraph() {
    	OClass vehicleClass = graphdb.createVertexType("GraphVehicle");
    	graphdb.createVertexType("GraphCar", vehicleClass);
    	graphdb.createVertexType("GraphMotocycle", "GraphVehicle");

        ODocument carNode = (ODocument) graphdb.createVertex("GraphCar").field("brand", "Hyundai")
                .field("model", "Coupe").field("year", 2003).save();
        ODocument motoNode = (ODocument) graphdb.createVertex("GraphMotocycle").field("brand", "Yamaha")
                .field("model", "X-City 250").field("year", 2009).save();

        graphdb.createEdge(carNode, motoNode).save();
        index();
    }

    @Transactional
    public static void save(String name, String description) {
        Item item = new Item();
        item.name = name;
        item.description = description;
        item.save();
        index();
    }

    @Transactional(db = DBTYPE.DOCUMENT)
    public static void good() {
        ODocument doc = new ODocument(docdb, "Account");
        doc.field("name", "good !!");
        doc.save();
        index();
    }

    @Transactional(db = DBTYPE.DOCUMENT)
    public static void bad() {
        ODocument doc = new ODocument(docdb, "Account");
        doc.field("name", "bad :(");
        doc.save();
        throw new RuntimeException("Hello from bad transaction, will be rolled back!");
    }

}