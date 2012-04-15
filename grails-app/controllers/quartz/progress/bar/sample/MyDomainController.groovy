package quartz.progress.bar.sample

import org.springframework.dao.DataIntegrityViolationException
import quartz.progressbar.QuartzProgressBar;

class MyDomainController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [myDomainInstanceList: MyDomain.list(params), myDomainInstanceTotal: MyDomain.count()]
    }

    def create() {
        [myDomainInstance: new MyDomain(params)]
    }

    def save() {
        def myDomainInstance = new MyDomain(params)
        if (!myDomainInstance.save(flush: true)) {
            render(view: "create", model: [myDomainInstance: myDomainInstance])
            return
        }
		def progBarId = QuartzProgressBar.execute{ ctx, progressBar ->
            progressBar.total = 42
            42.times{
                progressBar.step = it + 1
                progressBar.msg = "step ${progressBar.step} of ${progressBar.total}"
                sleep(128)
            }
            progressBar.msg = "done"
        }
        def progBarId2 = QuartzProgressBar.execute{ ctx, progressBar ->
            progressBar.total = 42
            12.times{
                progressBar.step = it + 1
                progressBar.msg = "step ${progressBar.step} of ${progressBar.total}"
                sleep(256)
            }
            progressBar.msg = "done"
            throw new RuntimeException("some error")
        }
        flash.message = message(code: 'default.created.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), myDomainInstance.id])
        redirect(action: "show", id: myDomainInstance.id, params : [progBarId: progBarId, progBarId2: progBarId2])
    }

    def show() {
        def myDomainInstance = MyDomain.get(params.id)
        if (!myDomainInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), params.id])
            redirect(action: "list")
            return
        }

        [myDomainInstance: myDomainInstance]
    }

    def edit() {
        def myDomainInstance = MyDomain.get(params.id)
        if (!myDomainInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), params.id])
            redirect(action: "list")
            return
        }

        [myDomainInstance: myDomainInstance]
    }

    def update() {
        def myDomainInstance = MyDomain.get(params.id)
        if (!myDomainInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (myDomainInstance.version > version) {
                myDomainInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'myDomain.label', default: 'MyDomain')] as Object[],
                          "Another user has updated this MyDomain while you were editing")
                render(view: "edit", model: [myDomainInstance: myDomainInstance])
                return
            }
        }

        myDomainInstance.properties = params

        if (!myDomainInstance.save(flush: true)) {
            render(view: "edit", model: [myDomainInstance: myDomainInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), myDomainInstance.id])
        redirect(action: "show", id: myDomainInstance.id)
    }

    def delete() {
        def myDomainInstance = MyDomain.get(params.id)
        if (!myDomainInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), params.id])
            redirect(action: "list")
            return
        }

        try {
            myDomainInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'myDomain.label', default: 'MyDomain'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
