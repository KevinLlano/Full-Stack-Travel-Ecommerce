import { Component, ViewChild, ChangeDetectorRef } from '@angular/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { MatSidenav } from '@angular/material/sidenav';
import { delay, filter } from 'rxjs/operators';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Vacation Dashboard';

  showCart = true;

  @ViewChild(MatSidenav)
  sidenav!: MatSidenav;
 
  constructor(private observer: BreakpointObserver, private cdr: ChangeDetectorRef) {}
 
  ngAfterViewInit() {
    // Use Promise.resolve() to push to next tick after view initialization
    Promise.resolve().then(() => {
      this.observer.observe(['(max-width: 800px)']).subscribe((res) => {
        if (res.matches) {
          this.sidenav.mode = 'over';
          this.sidenav.close();
        } else {
          this.sidenav.mode = 'side';
          this.sidenav.open();
        }
        // Manually trigger change detection after sidenav state changes
        this.cdr.detectChanges();
      });
    });
  }
}
