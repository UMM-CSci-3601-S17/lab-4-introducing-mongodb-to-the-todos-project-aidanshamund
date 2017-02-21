import { ComponentFixture, TestBed, async } from "@angular/core/testing";
import { Todo } from "./todo";
import { TodoComponent } from "./todo.component";
import { TodoListService } from "./todo-list.service";
import { Observable } from "rxjs";
import { PipeModule } from "../../pipe.module";

describe("Todo component", () => {

    let todoComponent: TodoComponent;
    let fixture: ComponentFixture<TodoComponent>;

    let todoListServiceStub: {
        getTodoById: (todoId: string) => Observable<Todo>
    };

    beforeEach(() => {
        // stub TodoService for test purposes
        todoListServiceStub = {
            getTodoById: (todoId: string) => Observable.of([
                {
                    _id: "58895985a22c04e761776d54",
                    owner: "Blanche",
                    status: false,
                    body: "In sunt ex non tempor cillum commodo amet incididunt anim qui commodo quis. Cillum non labore ex sint esse.",
                    category: "software design"
                },
                {
                    _id: "58895985c1849992336c219b",
                    owner: "Fry",
                    status: false,
                    body: "Ipsum esse est ullamco magna tempor anim laborum non officia deserunt veniam commodo. Aute minim incididunt ex commodo.",
                    category: "video games"
                },
                {
                    _id: "58895985ae3b752b124e7663",
                    owner: "Fry",
                    status: true,
                    body: "Ullamco irure laborum magna dolor non. Anim occaecat adipisicing cillum eu magna in.",
                    category: "homework"
                }
            ].find(todo => todo._id === todoId))
        };

        TestBed.configureTestingModule({
            imports: [PipeModule],
            declarations: [ TodoComponent ],
            providers:    [ { provide: TodoListService, useValue: todoListServiceStub } ]
        })
    });

    beforeEach(async(() => {
        TestBed.compileComponents().then(() => {
            fixture = TestBed.createComponent(TodoComponent);
            todoComponent = fixture.componentInstance;
        });
    }));

    it("can retrieve Blanche by ID", () => {
        todoComponent.setId("58895985a22c04e761776d54");
        expect(todoComponent.todo).toBeDefined();
        expect(todoComponent.todo.owner).toBe("Blanche");
        expect(todoComponent.todo.category).toBe("software design");
    });

    it("returns undefined for Santa", () => {
        todoComponent.setId("Santa");
        expect(todoComponent.todo).not.toBeDefined();
    });

});
