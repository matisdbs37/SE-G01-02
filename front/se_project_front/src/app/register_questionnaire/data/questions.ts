export interface Question {
    text: string;
    type: 'text' | 'yes_no' | 'slider';
    placeholder?: string;
    min?: number;
    max?: number;
}

export const QUESTIONS: Question[] = [
    {
        text: "What's your name ?", type: 'text', placeholder: "Enter your name"
    },
    {
        text: "What's your stress level ?", type: 'slider', min: 0, max: 10
    },
    {
        text: "Have you ever had meditation experiences ?", type:'yes_no'
    }
];