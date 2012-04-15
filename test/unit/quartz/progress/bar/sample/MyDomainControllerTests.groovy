package quartz.progress.bar.sample



import org.junit.*
import grails.test.mixin.*

@TestFor(MyDomainController)
@Mock(MyDomain)
class MyDomainControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/myDomain/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.myDomainInstanceList.size() == 0
        assert model.myDomainInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.myDomainInstance != null
    }

    void testSave() {
        controller.save()

        assert model.myDomainInstance != null
        assert view == '/myDomain/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/myDomain/show/1'
        assert controller.flash.message != null
        assert MyDomain.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/myDomain/list'


        populateValidParams(params)
        def myDomain = new MyDomain(params)

        assert myDomain.save() != null

        params.id = myDomain.id

        def model = controller.show()

        assert model.myDomainInstance == myDomain
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/myDomain/list'


        populateValidParams(params)
        def myDomain = new MyDomain(params)

        assert myDomain.save() != null

        params.id = myDomain.id

        def model = controller.edit()

        assert model.myDomainInstance == myDomain
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/myDomain/list'

        response.reset()


        populateValidParams(params)
        def myDomain = new MyDomain(params)

        assert myDomain.save() != null

        // test invalid parameters in update
        params.id = myDomain.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/myDomain/edit"
        assert model.myDomainInstance != null

        myDomain.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/myDomain/show/$myDomain.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        myDomain.clearErrors()

        populateValidParams(params)
        params.id = myDomain.id
        params.version = -1
        controller.update()

        assert view == "/myDomain/edit"
        assert model.myDomainInstance != null
        assert model.myDomainInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/myDomain/list'

        response.reset()

        populateValidParams(params)
        def myDomain = new MyDomain(params)

        assert myDomain.save() != null
        assert MyDomain.count() == 1

        params.id = myDomain.id

        controller.delete()

        assert MyDomain.count() == 0
        assert MyDomain.get(myDomain.id) == null
        assert response.redirectedUrl == '/myDomain/list'
    }
}
