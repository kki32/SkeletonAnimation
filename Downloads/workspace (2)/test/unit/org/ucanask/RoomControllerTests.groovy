package org.ucanask



import org.junit.*
import grails.test.mixin.*

@TestFor(RoomController)
@Mock(Room)
class RoomControllerTests {

	void testSomething() {
		assert true
	 }

//    def populateValidParams(params) {
//      assert params != null
//      // TODO: Populate valid properties like...
//      //params["name"] = 'someValidName'
//    }
//
//    void testCreate() {
//       def model = controller.create()
//       assert model.roomInstance != null
//    }
//
//    void testShow() {
//        controller.show()
//
//        assert flash.message != null
//        assert response.redirectedUrl == '/room/list'
//
//
//        populateValidParams(params)
//        def room = new Room(params)
//
//        assert room.save() != null
//
//        params.id = room.id
//
//        def model = controller.show()
//
//        assert model.roomInstance == room
//    }
//
//    void testEdit() {
//        controller.edit()
//
//        assert flash.message != null
//        assert response.redirectedUrl == '/room/list'
//
//
//        populateValidParams(params)
//        def room = new Room(params)
//
//        assert room.save() != null
//
//        params.id = room.id
//
//        def model = controller.edit()
//
//        assert model.roomInstance == room
//    }
//
//    void testUpdate() {
//        controller.update()
//
//        assert flash.message != null
//        assert response.redirectedUrl == '/room/list'
//
//        response.reset()
//
//
//        populateValidParams(params)
//        def room = new Room(params)
//
//        assert room.save() != null
//
//        // test invalid parameters in update
//        params.id = room.id
//        //TODO: add invalid values to params object
//
//        controller.update()
//
//        assert view == "/room/edit"
//        assert model.roomInstance != null
//
//        room.clearErrors()
//
//        populateValidParams(params)
//        controller.update()
//
//        assert response.redirectedUrl == "/room/show/$room.id"
//        assert flash.message != null
//
//        //test outdated version number
//        response.reset()
//        room.clearErrors()
//
//        populateValidParams(params)
//        params.id = room.id
//        params.version = -1
//        controller.update()
//
//        assert view == "/room/edit"
//        assert model.roomInstance != null
//        assert model.roomInstance.errors.getFieldError('version')
//        assert flash.message != null
//    }

//    void testDelete() {
//        controller.delete()
//        assert flash.message != null
//        assert response.redirectedUrl == '/room/list'
//
//        response.reset()
//
//        populateValidParams(params)
//        def room = new Room(params)
//
//        assert room.save() != null
//        assert Room.count() == 1
//
//        params.id = room.id
//
//        controller.delete()
//
//        assert Room.count() == 0
//        assert Room.get(room.id) == null
//        assert response.redirectedUrl == '/room/list'
//    }
}
