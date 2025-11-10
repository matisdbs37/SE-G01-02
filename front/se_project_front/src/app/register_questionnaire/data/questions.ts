export interface Question {
    text: string;
    type: 'text' | 'yes_no' | 'slider' | 'select';
    placeholder?: string;
    min?: number;
    max?: number;
    options?: string[];
}

export const QUESTIONS: Question[] = [
    {
        text: "What's your first name ?", type: 'text', placeholder: "Enter your first name"
    },
    {
        text: "What's your last name ?", type: 'text', placeholder: "Enter your last name"
    },
    {
        text: "Where are you from ?", type: 'select', placeholder: "Click to select a country", options: []
    },
    {
        text: "Do you agree to receive notifications ?", type:'yes_no'
    }
];