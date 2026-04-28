import { Component, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface ChatMessage {
  content: string;
  isUser: boolean;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewChecked {
  @ViewChild('chatBox') private chatBoxContainer!: ElementRef;

  messages: ChatMessage[] = [
    { content: 'Hello! I am your AI Knowledge Assistant. I have processed your document. What would you like to know?', isUser: false }
  ];
  currentQuestion: string = '';
  isLoading: boolean = false;

  constructor(private http: HttpClient) {}

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.chatBoxContainer.nativeElement.scrollTop = this.chatBoxContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }

  sendMessage() {
    const question = this.currentQuestion.trim();
    if (!question) return;

    // Add user message
    this.messages.push({ content: question, isUser: true });
    this.currentQuestion = '';
    this.isLoading = true;

    // Call Spring Boot API
    this.http.get('http://localhost:8080/ask', {
      params: { question },
      responseType: 'text'
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Basic markdown/newline formatting
        const formattedResponse = response.replace(/\n/g, '<br>');
        this.messages.push({ content: formattedResponse, isUser: false });
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error fetching response:', error);
        this.messages.push({ content: 'Sorry, there was an error processing your request. Please ensure the backend is running.', isUser: false });
      }
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append("file", file);

      this.messages.push({ content: `<i>Uploading file: ${file.name}...</i>`, isUser: false });

      this.http.post('http://localhost:8080/upload', formData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            this.messages.push({ content: `<b>Success:</b> ${response}`, isUser: false });
          },
          error: (err) => {
            console.error('Upload error:', err);
            this.messages.push({ content: `<b>Error:</b> Could not upload file.`, isUser: false });
          }
        });
    }
  }
}
